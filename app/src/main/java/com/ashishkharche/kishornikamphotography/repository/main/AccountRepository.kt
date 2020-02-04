package com.ashishkharche.kishornikamphotography.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.ashishkharche.kishornikamphotography.util.ApiSuccessResponse
import com.ashishkharche.kishornikamphotography.util.GenericApiResponse
import com.ashishkharche.kishornikamphotography.api.GenericResponse
import com.ashishkharche.kishornikamphotography.api.main.OpenApiMainService
import com.ashishkharche.kishornikamphotography.models.AccountProperties
import com.ashishkharche.kishornikamphotography.models.AuthToken
import com.ashishkharche.kishornikamphotography.persistence.AccountPropertiesDao
import com.ashishkharche.kishornikamphotography.repository.JobManager
import com.ashishkharche.kishornikamphotography.repository.NetworkBoundResource
import com.ashishkharche.kishornikamphotography.session.SessionManager
import com.ashishkharche.kishornikamphotography.ui.DataState
import com.ashishkharche.kishornikamphotography.ui.Response
import com.ashishkharche.kishornikamphotography.ui.ResponseType
import com.ashishkharche.kishornikamphotography.ui.main.account.state.AccountViewState
import com.ashishkharche.kishornikamphotography.util.*
import kotlinx.coroutines.*
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): JobManager()
{
    private val TAG: String = "AppDebug"

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            "getAccountProperties",
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ){

            // if network is down, view the cache and return
            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main){

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)

                createCacheRequestAndReturn()
            }

            override fun loadFromCache(): LiveData<AccountViewState> {

                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object: LiveData<AccountViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token!!}")
            }

            override suspend fun updateLocalDb(accountProp: AccountProperties?) {
                accountProp?.let {
                    accountPropertiesDao.updateAccountProperties(
                        accountProp.pk,
                        accountProp.email,
                        accountProp.username
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }


        }.asLiveData()
    }

    fun saveAccountProperties(authToken: AuthToken, accountProperties: AccountProperties): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, AccountProperties, AccountViewState>(
            "saveAccountProperties",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                updateLocalDb(null) // don't care about GenericResponse in local db

                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(null,
                            Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.saveAccountProperties(
                    "Token ${authToken.token!!}",
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                return accountPropertiesDao.updateAccountProperties(
                    accountProperties.pk,
                    accountProperties.email,
                    accountProperties.username
                )
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }

        }.asLiveData()
    }


    fun updatePassword(authToken: AuthToken, currentPassword: String, newPassword: String, confirmNewPassword: String): LiveData<DataState<AccountViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, AccountViewState>(
            "updatePassword",
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){

            // not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main){
                    // finish with success response
                    onCompleteJob(
                        DataState.data(null,
                            Response(response.body.response, ResponseType.Toast())
                        ))
                }
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AccountViewState> {
                return AbsentLiveData.create()
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.updatePassword(
                    "Token ${authToken.token!!}",
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

            override fun setJob(job: Job) {
                addJob(methodName, job)
            }

        }.asLiveData()
    }

}



















