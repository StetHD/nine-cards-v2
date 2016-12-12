package cards.nine.app.ui.applinks


import cards.nine.app.di.Injector
import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.{ApplicationTestData, SharedCollectionTestData}
import cards.nine.models.types.GetByName
import cards.nine.process.collection.CollectionProcess
import cards.nine.process.device.DeviceProcess
import cards.nine.process.intents.LauncherExecutorProcess
import cards.nine.process.trackevent.TrackEventProcess
import macroid.ActivityContextWrapper
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait AppLinksReceiverJobsSpecification
  extends TaskServiceSpecification
    with Mockito
    with SharedCollectionTestData
    with ApplicationTestData {


  trait AppLinksReceiverJobsScope
    extends Scope {

    implicit val contextWrapper = mock[ActivityContextWrapper]

    val mockInjector = mock[Injector]

    val mockAppLinksReceiverUiActions = mock[AppLinksReceiverUiActions]

    val mockLauncherExecutorProcess = mock[LauncherExecutorProcess]

    mockInjector.launcherExecutorProcess returns mockLauncherExecutorProcess

    val mockDeviceProcesss = mock[DeviceProcess]

    mockInjector.deviceProcess returns mockDeviceProcesss

    val mockCollectionProcess = mock[CollectionProcess]

    mockInjector.collectionProcess returns mockCollectionProcess

    val mockTrackEventProcess = mock[TrackEventProcess]

    mockInjector.trackEventProcess returns mockTrackEventProcess

    val appLinksReceiverJobs = new AppLinksReceiverJobs(mockAppLinksReceiverUiActions)(contextWrapper) {

      override lazy val di: Injector = mockInjector

      override def getString(res: Int, args: AnyRef*): String = ""

    }
  }
}

class AppLinksReceiverJobsSpec
  extends AppLinksReceiverJobsSpecification {

  "addCollection" should {
    "return a valid response when the service returns a right response" in new AppLinksReceiverJobsScope {

      mockDeviceProcesss.getSavedApps(any)(any) returns serviceRight(seqApplicationData)
      mockCollectionProcess.addCollection(any) returns serviceRight(collection)
      mockAppLinksReceiverUiActions.exit() returns serviceRight(Unit)

      appLinksReceiverJobs.addCollection(sharedCollection).mustRightUnit

      there was one(mockDeviceProcesss).getSavedApps(===(GetByName))(any)
      there was one(mockAppLinksReceiverUiActions).exit()
      there was one(mockCollectionProcess).addCollection(any)
    }
  }

  "showError" should {
    "shows an unexpected error message" in new AppLinksReceiverJobsScope {

      mockAppLinksReceiverUiActions.showUnexpectedErrorMessage() returns serviceRight(Unit)
      mockAppLinksReceiverUiActions.exit() returns serviceRight(Unit)

      appLinksReceiverJobs.showError().mustRightUnit

      there was one(mockAppLinksReceiverUiActions).exit()
      there was one(mockAppLinksReceiverUiActions).showUnexpectedErrorMessage()
    }
  }

  "shareCollection" should {
    "return a valid response when the service returns a right response" in new AppLinksReceiverJobsScope {

      mockLauncherExecutorProcess.launchShare(any)(any) returns serviceRight(Unit)
      mockAppLinksReceiverUiActions.exit() returns serviceRight(Unit)

      appLinksReceiverJobs.shareCollection(sharedCollection).mustRightUnit

      there was one(mockAppLinksReceiverUiActions).exit()
    }
  }
}