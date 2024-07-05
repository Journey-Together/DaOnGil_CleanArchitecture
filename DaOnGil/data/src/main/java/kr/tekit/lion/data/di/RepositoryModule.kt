package kr.tekit.lion.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.tekit.lion.data.repository.AreaCodeRepositoryImpl
import kr.tekit.lion.data.repository.AuthRepositoryImpl
import kr.tekit.lion.data.repository.KorWithRepositoryImpl
import kr.tekit.lion.data.repository.MemberRepositoryImpl
import kr.tekit.lion.data.repository.SigunguCodeRepositoryImpl
import kr.tekit.lion.domain.repository.AreaCodeRepository
import kr.tekit.lion.domain.repository.AuthRepository
import kr.tekit.lion.domain.repository.KorWithRepository
import kr.tekit.lion.domain.repository.MemberRepository
import kr.tekit.lion.domain.repository.SigunguCodeRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    abstract fun bindAreaCodeRepository(areaCodeRepositoryImpl: AreaCodeRepositoryImpl): AreaCodeRepository

    @Binds
    abstract fun bindKorWithRepository(korWithRepositoryImpl: KorWithRepositoryImpl): KorWithRepository

    @Binds
    abstract fun bindSigunguRepository(sigunguCodeRepositoryImpl: SigunguCodeRepositoryImpl): SigunguCodeRepository

    @Binds
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindMemberRepository(memberRepositoryImpl: MemberRepositoryImpl): MemberRepository
}