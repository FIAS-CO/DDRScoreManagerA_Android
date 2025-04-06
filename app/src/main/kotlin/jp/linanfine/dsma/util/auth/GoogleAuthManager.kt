package jp.linanfine.dsma.util.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import jp.linanfine.dsma.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.core.content.edit

class GoogleAuthManager private constructor() {
    companion object {
        private const val TAG = "GoogleAuthManager"
        private const val BASE_URL = "https://fnapi.fia-s.com/api"
        private val JSON = "application/json; charset=utf-8".toMediaType()

        @Volatile
        private var instance: GoogleAuthManager? = null

        fun getInstance(): GoogleAuthManager {
            return instance ?: synchronized(this) {
                instance ?: GoogleAuthManager().also { instance = it }
            }
        }
    }

    private val httpClient = OkHttpClient()
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("FlareNotePrefs", Context.MODE_PRIVATE)
    }

    /**
     * Google認証を実行してIDトークンを取得
     */
    private suspend fun authenticate(context: Context): String {
        try {
            val signInWithGoogleOption = GetSignInWithGoogleOption
                .Builder(BuildConfig.GOOGLE_CLIENT_ID)
                .build()

            val credentialManager = CredentialManager.create(context)
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()

            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            return when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential
                                .createFrom(credential.data)
                            googleIdTokenCredential.idToken
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e(TAG, "IDトークン解析エラー", e)
                            throw IOException("Googleトークン解析エラー: ${e.message}")
                        }
                    } else {
                        Log.e(TAG, "不明な認証情報タイプ: ${credential.type}")
                        throw IOException("不明な認証情報タイプ")
                    }
                }
                else -> {
                    Log.e(TAG, "想定外の認証情報: ${credential.javaClass.simpleName}")
                    throw IOException("想定外の認証情報タイプ")
                }
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Google認証エラー", e)
            throw IOException("Google認証エラー: ${e.message}")
        }
    }

    /**
     * Google認証でユーザーを検索する
     */
    private suspend fun findUserWithGoogle(context: Context): FindUserResult {
        val idToken = authenticate(context)
        return findUserByGoogleToken(idToken)
    }

    fun findUserWithGoogleSync(context: Context): FindUserResult {
        return runBlocking {
            findUserWithGoogle(context)
        }
    }

    /**
     * 既存ユーザーとGoogleアカウントを連携する
     */
    private suspend fun connectWithGoogle(context: Context, userId: String): User {
        val idToken = authenticate(context)
        return connectUserWithGoogle(userId, idToken)
    }

    fun connectWithGoogleSync(context: Context, userId: String): User {
        return runBlocking {
            connectWithGoogle(context, userId)
        }
    }

    /**
     * Googleアカウントからユーザーを検索
     */
    private suspend fun findUserByGoogleToken(idToken: String): FindUserResult = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("googleToken", idToken)
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/find-player-by-google")
                .post(requestBody.toString().toRequestBody(JSON))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("サーバーエラー: ${response.code}")
                }

                val responseBody = response.body?.string() ?: throw IOException("レスポンスボディが空です")
                val json = JSONObject(responseBody)

                val result = FindUserResult().apply {
                    success = json.optBoolean("success", false)
                    found = json.optBoolean("found", false)

                    if (success && found) {
                        val playerJson = json.getJSONObject("player")
                        player = User(
                            playerJson.getString("id"),
                            playerJson.getString("name")
                        )
                    }
                }

                result
            }
        } catch (e: Exception) {
            when (e) {
                is JSONException -> throw IOException("リクエストまたはレスポンスの解析に失敗しました: ${e.message}")
                else -> throw e
            }
        }
    }

    /**
     * ユーザーとGoogleアカウントを紐づける
     */
    private suspend fun connectUserWithGoogle(userId: String, idToken: String): User = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("playerId", userId)
                put("googleToken", idToken)
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/connect-google")
                .post(requestBody.toString().toRequestBody(JSON))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("サーバーエラー: ${response.code}")
                }

                val responseBody = response.body?.string() ?: throw IOException("レスポンスボディが空です")
                val json = JSONObject(responseBody)

                val success = json.optBoolean("success", false)
                if (success && json.has("player")) {
                    val playerJson = json.getJSONObject("player")
                    User(
                        playerJson.getString("id"),
                        playerJson.getString("name")
                    )
                } else {
                    val error = json.optString("error", "不明なエラー")
                    throw IOException(error)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is JSONException -> throw IOException("リクエストまたはレスポンスの解析に失敗しました: ${e.message}")
                else -> throw e
            }
        }
    }

    /**
     * Googleアカウントとの紐づけを解除
     */
    private suspend fun disconnectGoogle(userId: String): String = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("playerId", userId)
            }

            val request = Request.Builder()
                .url("$BASE_URL/auth/unlink-google")
                .post(requestBody.toString().toRequestBody(JSON))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("サーバーエラー: ${response.code}")
                }

                val responseBody = response.body?.string() ?: throw IOException("レスポンスボディが空です")
                val json = JSONObject(responseBody)

                val success = json.optBoolean("success", false)
                if (success) {
                    json.optString("message", "Google連携を解除しました")
                } else {
                    val error = json.optString("error", "不明なエラー")
                    throw IOException(error)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is JSONException -> throw IOException("リクエストまたはレスポンスの解析に失敗しました: ${e.message}")
                else -> throw e
            }
        }
    }

    fun disconnectGoogleSync(userId: String): String {
        return runBlocking {
            disconnectGoogle(userId)
        }
    }

    /**
     * サーバー上のユーザー状態を検証
     */
    private suspend fun validateUser(userId: String): UserValidationResult = withContext(Dispatchers.IO) {
        try {
            val requestBody = JSONObject().apply {
                put("id", userId)
            }

            val request = Request.Builder()
                .url("$BASE_URL/validate-user")
                .post(requestBody.toString().toRequestBody(JSON))
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("サーバーエラー: ${response.code}")
                }

                val responseBody = response.body?.string() ?: throw IOException("レスポンスボディが空です")
                val json = JSONObject(responseBody)

                UserValidationResult().apply {
                    exists = json.optBoolean("exists", false)
                    isGoogleLinked = json.optBoolean("isGoogleLinked", false)
                }
            }
        } catch (e: Exception) {
            when (e) {
                is JSONException -> throw IOException("リクエストまたはレスポンスの解析に失敗しました: ${e.message}")
                else -> throw e
            }
        }
    }

    fun validateUserSync(userId: String): UserValidationResult {
        return runBlocking {
            validateUser(userId)
        }
    }

    /**
     * Google連携状態をローカルに保存
     */
    fun saveGoogleLinkStatus(userId: String, isLinked: Boolean) {
        prefs.edit { putBoolean("google_linked_$userId", isLinked) }
    }

    /**
     * Google連携状態をローカルから取得
     */
    fun getGoogleLinkStatus(userId: String): Boolean {
        return prefs.getBoolean("google_linked_$userId", false)
    }

    // 内部クラス定義
    data class User(val id: String, val name: String)

    class FindUserResult {
        var success: Boolean = false
        var found: Boolean = false
        var player: User? = null
    }

    class UserValidationResult {
        var exists: Boolean = false
        var isGoogleLinked: Boolean = false
    }
}