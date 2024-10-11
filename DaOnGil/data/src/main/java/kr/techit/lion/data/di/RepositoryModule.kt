package kr.techit.lion.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.techit.lion.data.repository.AedRepositoryImpl
import kr.techit.lion.data.repository.AppThemeRepositoryImpl
import kr.techit.lion.data.repository.AreaCodeRepositoryImpl
import kr.techit.lion.data.repository.AuthRepositoryImpl
import kr.techit.lion.data.repository.BookmarkRepositoryImpl
import kr.techit.lion.data.repository.EmergencyRepositoryImpl
import kr.techit.lion.data.repository.ActivationRepositoryImpl
import kr.techit.lion.data.repository.KorWithRepositoryImpl
import kr.techit.lion.data.repository.MemberRepositoryImpl
import kr.techit.lion.data.repository.NaverMapRepositoryImpl
import kr.techit.lion.data.repository.PharmacyRepositoryImpl
import kr.techit.lion.data.repository.PlaceRepositoryImpl
import kr.techit.lion.data.repository.PlanRepositoryImpl
import kr.techit.lion.data.repository.RecentlySearchKeywordRepositoryImpl
import kr.techit.lion.data.repository.ReportRepositoryImpl
import kr.techit.lion.data.repository.SigunguCodeRepositoryImpl
import kr.techit.lion.domain.repository.AedRepository
import kr.techit.lion.domain.repository.AppThemeRepository
import kr.techit.lion.domain.repository.AreaCodeRepository
import kr.techit.lion.domain.repository.AuthRepository
import kr.techit.lion.domain.repository.BookmarkRepository
import kr.techit.lion.domain.repository.EmergencyRepository
import kr.techit.lion.domain.repository.ActivationRepository
import kr.techit.lion.domain.repository.KorWithRepository
import kr.techit.lion.domain.repository.MemberRepository
import kr.techit.lion.domain.repository.NaverMapRepository
import kr.techit.lion.domain.repository.PharmacyRepository
import kr.techit.lion.domain.repository.PlaceRepository
import kr.techit.lion.domain.repository.PlanRepository
import kr.techit.lion.domain.repository.RecentlySearchKeywordRepository
import kr.techit.lion.domain.repository.ReportRepository
import kr.techit.lion.domain.repository.SigunguCodeRepository

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