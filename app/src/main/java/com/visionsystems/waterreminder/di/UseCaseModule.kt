package com.visionsystems.waterreminder.di

import com.visionsystems.waterreminder.domain.usecase.aboutyou.AboutYouUseCase
import com.visionsystems.waterreminder.domain.usecase.aboutyou.AboutYouUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.addwater.AddWaterUseCase
import com.visionsystems.waterreminder.domain.usecase.addwater.AddWaterUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCase
import com.visionsystems.waterreminder.domain.usecase.drink.DrinkUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.editprofile.EditProfileUseCase
import com.visionsystems.waterreminder.domain.usecase.editprofile.EditProfileUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.goalreached.GoalReachedUseCase
import com.visionsystems.waterreminder.domain.usecase.goalreached.GoalReachedUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.goalready.GoalReadyUseCase
import com.visionsystems.waterreminder.domain.usecase.goalready.GoalReadyUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.home.HomeUseCase
import com.visionsystems.waterreminder.domain.usecase.home.HomeUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.insights.InsightsUseCase
import com.visionsystems.waterreminder.domain.usecase.insights.InsightsUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.onboarding.OnboardingUseCase
import com.visionsystems.waterreminder.domain.usecase.onboarding.OnboardingUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.profile.ProfileUseCase
import com.visionsystems.waterreminder.domain.usecase.profile.ProfileUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.quickadd.QuickAddUseCase
import com.visionsystems.waterreminder.domain.usecase.quickadd.QuickAddUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.reminders.RemindersUseCase
import com.visionsystems.waterreminder.domain.usecase.reminders.RemindersUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.schedule.ScheduleUseCase
import com.visionsystems.waterreminder.domain.usecase.schedule.ScheduleUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.setgoal.SetGoalUseCase
import com.visionsystems.waterreminder.domain.usecase.setgoal.SetGoalUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.signin.SignInUseCase
import com.visionsystems.waterreminder.domain.usecase.signin.SignInUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.signup.SignUpUseCase
import com.visionsystems.waterreminder.domain.usecase.signup.SignUpUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.splash.SplashUseCase
import com.visionsystems.waterreminder.domain.usecase.splash.SplashUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.verifyemail.VerifyEmailUseCase
import com.visionsystems.waterreminder.domain.usecase.verifyemail.VerifyEmailUseCaseImpl
import com.visionsystems.waterreminder.domain.usecase.widget.WidgetUseCase
import com.visionsystems.waterreminder.domain.usecase.widget.WidgetUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCaseModule {

    @Binds
    fun bindDrinkUseCase(impl: DrinkUseCaseImpl): DrinkUseCase

    @Binds
    fun bindSplashUseCase(impl: SplashUseCaseImpl): SplashUseCase

    @Binds
    fun bindOnboardingUseCase(impl: OnboardingUseCaseImpl): OnboardingUseCase

    @Binds
    fun bindSignInUseCase(impl: SignInUseCaseImpl): SignInUseCase

    @Binds
    fun bindSignUpUseCase(impl: SignUpUseCaseImpl): SignUpUseCase

    @Binds
    fun bindVerifyEmailUseCase(impl: VerifyEmailUseCaseImpl): VerifyEmailUseCase

    @Binds
    fun bindAboutYouUseCase(impl: AboutYouUseCaseImpl): AboutYouUseCase

    @Binds
    fun bindScheduleUseCase(impl: ScheduleUseCaseImpl): ScheduleUseCase

    @Binds
    fun bindGoalReadyUseCase(impl: GoalReadyUseCaseImpl): GoalReadyUseCase

    @Binds
    fun bindHomeUseCase(impl: HomeUseCaseImpl): HomeUseCase

    @Binds
    fun bindAddWaterUseCase(impl: AddWaterUseCaseImpl): AddWaterUseCase

    @Binds
    fun bindGoalReachedUseCase(impl: GoalReachedUseCaseImpl): GoalReachedUseCase

    @Binds
    fun bindInsightsUseCase(impl: InsightsUseCaseImpl): InsightsUseCase

    @Binds
    fun bindRemindersUseCase(impl: RemindersUseCaseImpl): RemindersUseCase

    @Binds
    fun bindProfileUseCase(impl: ProfileUseCaseImpl): ProfileUseCase

    @Binds
    fun bindSetGoalUseCase(impl: SetGoalUseCaseImpl): SetGoalUseCase

    @Binds
    fun bindEditProfileUseCase(impl: EditProfileUseCaseImpl): EditProfileUseCase

    @Binds
    fun bindWidgetUseCase(impl: WidgetUseCaseImpl): WidgetUseCase

    @Binds
    fun bindQuickAddUseCase(impl: QuickAddUseCaseImpl): QuickAddUseCase
}
