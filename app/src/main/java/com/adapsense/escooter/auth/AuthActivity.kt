package com.adapsense.escooter.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.adapsense.escooter.R
import com.adapsense.escooter.base.BaseActivity
import com.adapsense.escooter.home.HomeActivity
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_splash.container
import kotlinx.android.synthetic.main.item_user.*

class AuthActivity : BaseActivity(), AuthContract.View {

    private lateinit var authPresenter: AuthContract.Presenter

    private var code: String? = ""
    private var login: Boolean? = false
    private var rider: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        authPresenter = AuthPresenter(this)
    }

    override fun onResume() {
        super.onResume()
        authPresenter.start()

        code = intent?.extras?.getString("code")
        login = intent?.extras?.getBoolean("login")
        rider = intent?.extras?.getBoolean("rider")

        if(rider != null && !rider!!) {
            showLogin(false)
            showAdmin()
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_right);
    }

    override fun setPresenter(presenter: AuthContract.Presenter?) {
        authPresenter = presenter!!
    }

    override fun setupViews() {

        emailTextInputEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search.performClick()
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(passwordTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        passwordTextInputEditText.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginSignup.performClick()
                val inputMethodManager =
                    getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(passwordTextInputEditText.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })



        search.setOnClickListener {
            showProgressDialog()
            authPresenter.search(emailTextInputEditText.text.toString().trim())
        }

        loginSignup.setOnClickListener {

            showProgressDialog()
            val fullName = fullNameTextInputEditText.text.toString().trim()
            val emailAddress = emailTextInputEditText.text.toString().trim()
            val password = passwordTextInputEditText.text.toString().trim()

            if(rider != null && rider!!) {
                if(login != null && login!!) {
                    authPresenter.signInRider(emailAddress, password)
                } else {
                    authPresenter.signUpRider(fullName, emailAddress, password)
                }
            } else {
                authPresenter.signInAdmin(emailAddress, password)
            }
        }

        scanScooter.setOnClickListener {
            onBackPressed()
        }

        loginAsAdmin.setOnClickListener {
            login = true
            rider = !rider!!

            fullNameTextInputLayout.visibility = View.GONE
            if(rider!!) {
                emailTextInputEditText.imeOptions = EditorInfo.IME_ACTION_DONE
                passwordTextInputLayout.visibility = View.GONE
                search.visibility = View.VISIBLE
                loginSignup.visibility = View.GONE
                loginAsAdmin.text = "Login as Admin"
            } else {
                emailTextInputEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
                emailTextInputEditText.nextFocusForwardId = R.id.passwordTextInputEditText
                passwordTextInputEditText.imeOptions = EditorInfo.IME_ACTION_DONE
                passwordTextInputLayout.visibility = View.VISIBLE
                search.visibility = View.GONE
                loginSignup.visibility = View.VISIBLE
                loginSignup.text = "Login as Admin"
                loginAsAdmin.text = "Login as Rider"
            }
        }
    }

    override fun showLogin(isRider: Boolean) {
        dismissDialogs()
        login = true
        rider = isRider
        val containerConstraintSet = ConstraintSet()
        containerConstraintSet.clone(container)
        val loginConstraintSet = ConstraintSet()
        loginConstraintSet.clone(this, R.layout.activity_auth_login)
        TransitionManager.beginDelayedTransition(container)
        loginConstraintSet.applyTo(container)
        fullNameTextInputLayout.visibility = View.GONE
        passwordTextInputLayout.visibility = View.VISIBLE
        passwordTextInputEditText.requestFocus()
        search.visibility = View.GONE
        loginSignup.visibility = View.VISIBLE
        loginSignup.text = if (isRider) "Login as Rider" else "Login as Admin"
        loginAsAdmin.text = "Login as Admin"
    }

    override fun showAdmin() {
        emailTextInputEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        emailTextInputEditText.nextFocusForwardId = R.id.passwordTextInputEditText
        emailTextInputEditText.requestFocus()
        passwordTextInputEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        adminContainer.visibility = View.GONE
    }

    override fun showSignup() {
        dismissDialogs()
        login = false
        rider = true
        val containerConstraintSet = ConstraintSet()
        containerConstraintSet.clone(container)
        val signupConstraintSet = ConstraintSet()
        signupConstraintSet.clone(this, R.layout.activity_auth_signup)
        TransitionManager.beginDelayedTransition(container)
        signupConstraintSet.applyTo(container)
        fullNameTextInputLayout.visibility = View.VISIBLE
        fullNameTextInputEditText.requestFocus()
        emailTextInputEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
        emailTextInputEditText.nextFocusForwardId = R.id.passwordTextInputEditText
        passwordTextInputEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        passwordTextInputLayout.visibility = View.VISIBLE
        search.visibility = View.GONE
        loginSignup.visibility = View.VISIBLE
        loginSignup.text = "Signup as Rider"
        loginAsAdmin.text = "Login as Admin"
    }

    override fun showHome() {
        dismissDialogs()
        startActivity(Intent(this, HomeActivity::class.java))
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        finish()
    }

    override fun showErrorFullName(error: String?) {
        if(!error.isNullOrEmpty()) {
            dismissDialogs()
        }
        fullNameTextInputLayout.error = error
    }

    override fun showErrorEmailAddress(error: String?) {
        if(!error.isNullOrEmpty()) {
            dismissDialogs()
        }
        emailTextInputLayout.error = error
    }

    override fun showErrorPassword(error: String?) {
        if(!error.isNullOrEmpty()) {
            dismissDialogs()
        }
        passwordTextInputLayout.error = error
    }

    override fun showError(title: String, message: String) {
        showAlertDialog(title, message)
    }

    override fun showMessage(message: String) {
        dismissDialogs()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}