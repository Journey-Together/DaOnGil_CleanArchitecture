package kr.tekit.lion.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.repository.AedRepositoryImpl
import kr.tekit.lion.data.repository.AppThemeRepositoryImpl
import kr.tekit.lion.data.repository.AreaCodeRepositoryImpl
import kr.tekit.lion.data.repository.AuthRepositoryImpl
import kr.tekit.lion.data.repository.BookmarkRepositoryImpl
import kr.tekit.lion.data.repository.EmergencyRepositoryImpl
import kr.tekit.lion.data.repository.ActivationRepositoryImpl
import kr.tekit.lion.data.repository.KorWithRepositoryImpl
import kr.tekit.lion.data.repository.MemberRepositoryImpl
import kr.tekit.lion.data.repository.NaverMapRepositoryImpl
import kr.tekit.lion.data.repository.PharmacyRepositoryImpl
import kr.tekit.lion.data.repository.PlaceRepositoryImpl
import kr.tekit.lion.data.repository.PlanRepositoryImpl
import kr.tekit.lion.data.repository.RecentlySearchKeywordRepositoryImpl
import kr.tekit.lion.data.repository.ReportRepositoryImpl
import kr.tekit.lion.data.repository.SigunguCodeRepositoryImpl
import kr.tekit.lion.domain.repository.AedRepository
import kr.tekit.lion.domain.repository.AppThemeRepository
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.BookmarkRepository
import kr.tekit.lion.domain.repository.EmergencyRepository
import kr.tekit.lion.domain.repository.ActivationRepository
import kr.tekit.lion.domain.repository.KorWithRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.domain.repository.NaverMapRepository
import kr.tekit.lion.domain.repository.PharmacyRepository
import kr.tekit.lion.domain.repository.PlaceRepository
import kr.tekit.lion.domain.repository.PlanRepository
import kr.tekit.lion.domain.repository.RecentlySearchKeywordRepository
import kr.tekit.lion.domain.repository.ReportRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository

@Module
@InstallIn(SingletonComponent::class)
internal interface RepositoryModule {

    @Binds
    fun bindAreaCodeRepository(areaCodeRepositoryImpl: AreaCodeRepositoryImpl): AreaCodeRepository

    @Binds
    fun bindKorWithRepository(korWithRepositoryImpl: KorWithRepositoryImpl): KorWithRepository

    @Binds
    fun bindSigunguRepository(sigunguCodeRepositoryImpl: SigunguCodeRepositoryImpl): SigunguCodeRepository

    @Binds
    fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    fun bindMemberRepository(memberRepositoryImpl: MemberRepositoryImpl): MemberRepository

    @Binds
    fun bindPlaceRepository(placeRepositoryImpl: PlaceRepositoryImpl): PlaceRepository

    @Binds
    fun bindRecentlySearchKeywordRepository(recentlySearchKeywordRepositoryImpl: RecentlySearchKeywordRepositoryImpl)
    : RecentlySearchKeywordRepository

    @Binds
    fun bindAppThemeRepository(appThemeRepositoryImpl: AppThemeRepositoryImpl): AppThemeRepository

    @Binds
    fun bindBookmarkRepository(bookmarkRepositoryImpl: BookmarkRepositoryImpl): BookmarkRepository

    @Binds
    fun bindNaverMapRepository(naverMapRepositoryImpl: NaverMapRepositoryImpl): NaverMapRepository

    @Binds
    fun bindAedRepository(aedRepositoryImpl: AedRepositoryImpl): AedRepository

    @Binds
    fun bindEmergencyRepository(emergencyRepositoryImpl: EmergencyRepositoryImpl): EmergencyRepository

    @Binds
    fun bindPharmacyRepository(pharmacyRepositoryImpl: PharmacyRepositoryImpl): PharmacyRepository

    @Binds
    fun bindPlanRepository(planRepositoryImpl: PlanRepositoryImpl): PlanRepository

    @Binds
    fun bindReportRepository(reportRepositoryImpl: ReportRepositoryImpl): ReportRepository
  
    @Binds
    fun bindFirstLogInRepository(activationRepositoryImpl: ActivationRepositoryImpl): ActivationRepository
}