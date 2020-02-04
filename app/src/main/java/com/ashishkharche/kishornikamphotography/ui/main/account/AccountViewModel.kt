package com.ashishkharche.kishornikamphotography.ui.main.account

import androidx.lifecycle.*
import com.ashishkharche.kishornikamphotography.models.AccountProperties
import com.ashishkharche.kishornikamphotography.repository.main.AccountRepository
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.BaseViewModel
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Loading
import com.ashishkharche.kishornikamphotography.ui.main.account.state.AccountStateEvent
import com.ashishkharche.kishornikamphotography.ui.main.account.state.AccountStateEvent.*
import com.ashishkharche.kishornikamphotography.ui.main.account.state.AccountViewState
import com.ashishkharche.kishornikamphotography.util.*
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
)
    : BaseViewModel<AccountStateEvent, AccountViewState>()
{

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){
            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }
            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        val newAccountProperties = AccountProperties(
                            pk,
                            stateEvent.email,
                            stateEvent.username
                        )
                        accountRepository.saveAccountProperties(
                            authToken,
                            newAccountProperties
                        )
                    }
                }?: AbsentLiveData.create()
            }
            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword
                    )
                }?: AbsentLiveData.create()
            }
            is None ->{
                return object: LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties){
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logout(){
        sessionManager.logout()
    }

    fun cancelActiveJobs(){
        accountRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }



}















