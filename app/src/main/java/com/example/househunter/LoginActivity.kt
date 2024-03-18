package com.example.househunter

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.househunter.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.NaverIdLoginSDK.oauthLoginCallback
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class LoginActivity : AppCompatActivity(){

    private lateinit var auth : FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    private var email: String = ""
    private var phone: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.Email_Edit)
        val etPass = findViewById<EditText>(R.id.Pass_Edit)
        val btnLogin = findViewById<Button>(R.id.LoginpBtn)
        val btnSignUp = findViewById<Button>(R.id.SignupBtn)

        val naverClientId = getString(R.string.naver_client_id)
        val naverClientSecret = getString(R.string.naver_client_secret)
        val naverClientName = getString(R.string.naver_client_name)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)

        binding.buttonOAuthLoginImg.setOnClickListener {
            startNaverLogin()
        }

        // 회원가입 버튼 클릭
        btnSignUp.setOnClickListener{
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
        }


        //로그인 버튼
        btnLogin.setOnClickListener{
            val email = etEmail.text.toString().trim()
            val password = etPass.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(this@LoginActivity,"이메일과 비밀번호를 입력하시오", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful){
                        //로그인 성공 시 매인 액티비티로 이동
                        Toast.makeText(this@LoginActivity,"로그인성공",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }else{
                        //로그인 실패 시 사용자에게 메시지 표시
                        if(task.exception is FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(this@LoginActivity, "이메일 또는 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(this@LoginActivity, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
    private fun startNaverLogin(){
        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val userId = response.profile?.id
                val userEmail = response.profile?.email
                val userName = response.profile?.name
                // 사용자 정보를 파이어베이스에 저장
                saveUserToFirebase(userId, userEmail, userName)
                Toast.makeText(this@LoginActivity, "네이버 아이디 로그인 성공!", Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@LoginActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    private fun saveUserToFirebase(uid: String?, email: String?, name: String?) {
        uid?.let { uid ->
            val userMap = HashMap<String, Any>()
            userMap["uid"] = uid ?:""
            userMap["email"] = email ?: ""
            userMap["name"] = name ?: ""

            val database: DatabaseReference = FirebaseDatabase.getInstance().reference
            database.child("users").child(uid).setValue(userMap)
                .addOnSuccessListener {
                    Log.d("LoginActivity", "User data saved to Firebase")
                }
                .addOnFailureListener { e ->
                    Log.w("LoginActivity", "Error saving user data to Firebase", e)
                }
        }
    }

}