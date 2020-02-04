package com.ashishkharche.kishornikamphotography.ui.auth

import android.util.Log
import androidx.lifecycle.*
import com.ashishkharche.kishornikamphotography.models.AuthToken
import com.ashishkharche.kishornikamphotography.repository.auth.AuthRepository
import com.ashishkharche.kishornikamphotography.ui.BaseViewModel
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.auth.state.*
import com.ashishkharche.kishornikamphotography.ui.auth.state.AuthStateEvent.*
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthStateEvent, AuthViewState>()
{
    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when(stateEvent){

            is LoginAttemptEvent -> {
                Log.d(TAG, "handleStateEvent: attempting login... ")
                return authRepository.attemptLogin(
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                Log.d(TAG, "handleStateEvent: attempting registration...")
                return authRepository.attemptRegistration(
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }


        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJobs()
    }


    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}




























