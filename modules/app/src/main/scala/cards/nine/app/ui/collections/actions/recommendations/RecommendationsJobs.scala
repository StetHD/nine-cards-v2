package cards.nine.app.ui.collections.actions.recommendations

import cards.nine.app.commons.NineCardIntentConversions
import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import cards.nine.process.recommendations.models.RecommendedApp
import cards.nine.models.types.NineCardCategory
import macroid.ActivityContextWrapper

class RecommendationsJobs(
  category: NineCardCategory,
  packages: Seq[String],
  actions: RecommendationsUiActions)(implicit activityContextWrapper: ActivityContextWrapper)
  extends Jobs
  with NineCardIntentConversions {

  def initialize(): TaskService[Unit] = for {
    _ <- actions.initialize()
    _ <- loadRecommendations()
  } yield ()

  def installNow(app: RecommendedApp): TaskService[Unit] =
    for {
      _ <- di.launcherExecutorProcess.launchGooglePlay(app.packageName)
      _ <- actions.recommendationAdded(app)
    } yield ()

  def loadRecommendations(): TaskService[Unit] = {
    for {
      _ <- actions.showLoading()
      recommendations <- if (category.isAppCategory) {
        di.recommendationsProcess.getRecommendedAppsByCategory(category, packages)
      } else {
        di.recommendationsProcess.getRecommendedAppsByPackages(packages, packages)
      }
      _ <- actions.loadRecommendations(recommendations)
    } yield ()
  }

  def showErrorLoadingRecommendation(): TaskService[Unit] = actions.showErrorLoadingRecommendationInScreen()

  def showError(): TaskService[Unit] = actions.showContactUsError()

  def close(): TaskService[Unit] = actions.close()

}