package rehabilitationsupportapp.us.di

import com.tanvir.reminder.data.repository.ApiRepositoryImpl
import com.tanvir.reminder.domain.repository.ApiRepository
import com.tanvir.reminder.data.remote.api_service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ApiNetworkModule {
    @Provides
    @ViewModelScoped
    fun providesAuthRepository(api: ApiService): ApiRepository {
        return ApiRepositoryImpl(api)
    }

}