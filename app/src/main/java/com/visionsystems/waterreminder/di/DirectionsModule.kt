package com.visionsystems.waterreminder.di

import com.visionsystems.waterreminder.presenter.screens.aboutyou.AboutYouContract
import com.visionsystems.waterreminder.presenter.screens.aboutyou.AboutYouDirection
import com.visionsystems.waterreminder.presenter.screens.addwater.AddWaterContract
import com.visionsystems.waterreminder.presenter.screens.addwater.AddWaterDirection
import com.visionsystems.waterreminder.presenter.screens.editprofile.EditProfileContract
import com.visionsystems.waterreminder.presenter.screens.editprofile.EditProfileDirection
import com.visionsystems.waterreminder.presenter.screens.goalreached.GoalReachedContract
import com.visionsystems.waterreminder.presenter.screens.goalreached.GoalReachedDirection
import com.visionsystems.waterreminder.presenter.screens.goalready.GoalReadyContract
import com.visionsystems.waterreminder.presenter.screens.goalready.GoalReadyDirection
import com.visionsystems.waterreminder.presenter.screens.home.HomeContract
import com.visionsystems.waterreminder.presenter.screens.home.HomeDirection
import com.visionsystems.waterreminder.presenter.screens.insights.InsightsContract
import com.visionsystems.waterreminder.presenter.screens.insights.InsightsDirection
import com.visionsystems.waterreminder.presenter.screens.main.MainContract
import com.visionsystems.waterreminder.presenter.screens.main.MainDirection
import com.visionsystems.waterreminder.presenter.screens.onboarding.OnboardingContract
import com.visionsystems.waterreminder.presenter.screens.onboarding.OnboardingDirection
import com.visionsystems.waterreminder.presenter.screens.profile.ProfileContract
import com.visionsystems.waterreminder.presenter.screens.profile.ProfileDirection
import com.visionsystems.waterreminder.presenter.screens.quickadd.QuickAddContract
import com.visionsystems.waterreminder.presenter.screens.quickadd.QuickAddDirection
import com.visionsystems.waterreminder.presenter.screens.reminders.RemindersContract
import com.visionsystems.waterreminder.presenter.screens.reminders.RemindersDirection
import com.visionsystems.waterreminder.presenter.screens.schedule.ScheduleContract
import com.visionsystems.waterreminder.presenter.screens.schedule.ScheduleDirection
import com.visionsystems.waterreminder.presenter.screens.setgoal.SetGoalContract
import com.visionsystems.waterreminder.presenter.screens.setgoal.SetGoalDirection
import com.visionsystems.waterreminder.presenter.screens.signin.SignInContract
import com.visionsystems.waterreminder.presenter.screens.signin.SignInDirection
import com.visionsystems.waterreminder.presenter.screens.signup.SignUpContract
import com.visionsystems.waterreminder.presenter.screens.signup.SignUpDirection
import com.visionsystems.waterreminder.presenter.screens.verifyemail.VerifyEmailContract
import com.visionsystems.waterreminder.presenter.screens.verifyemail.VerifyEmailDirection
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface DirectionsModule {

    @Binds
    @ViewModelScoped
    fun bindOnboardingDirection(impl: OnboardingDirection): OnboardingContract.Direction

    @Binds
    @ViewModelScoped
    fun bindSignInDirection(impl: SignInDirection): SignInContract.Direction

    @Binds
    @ViewModelScoped
    fun bindSignUpDirection(impl: SignUpDirection): SignUpContract.Direction

    @Binds
    @ViewModelScoped
    fun bindVerifyEmailDirection(impl: VerifyEmailDirection): VerifyEmailContract.Direction

    @Binds
    @ViewModelScoped
    fun bindAboutYouDirection(impl: AboutYouDirection): AboutYouContract.Direction

    @Binds
    @ViewModelScoped
    fun bindScheduleDirection(impl: ScheduleDirection): ScheduleContract.Direction

    @Binds
    @ViewModelScoped
    fun bindGoalReadyDirection(impl: GoalReadyDirection): GoalReadyContract.Direction

    @Binds
    @ViewModelScoped
    fun bindMainDirection(impl: MainDirection): MainContract.Direction

    @Binds
    @ViewModelScoped
    fun bindHomeDirection(impl: HomeDirection): HomeContract.Direction

    @Binds
    @ViewModelScoped
    fun bindAddWaterDirection(impl: AddWaterDirection): AddWaterContract.Direction

    @Binds
    @ViewModelScoped
    fun bindGoalReachedDirection(impl: GoalReachedDirection): GoalReachedContract.Direction

    @Binds
    @ViewModelScoped
    fun bindInsightsDirection(impl: InsightsDirection): InsightsContract.Direction

    @Binds
    @ViewModelScoped
    fun bindRemindersDirection(impl: RemindersDirection): RemindersContract.Direction

    @Binds
    @ViewModelScoped
    fun bindProfileDirection(impl: ProfileDirection): ProfileContract.Direction

    @Binds
    @ViewModelScoped
    fun bindSetGoalDirection(impl: SetGoalDirection): SetGoalContract.Direction

    @Binds
    @ViewModelScoped
    fun bindEditProfileDirection(impl: EditProfileDirection): EditProfileContract.Direction

    @Binds
    @ViewModelScoped
    fun bindQuickAddDirection(impl: QuickAddDirection): QuickAddContract.Direction
}
