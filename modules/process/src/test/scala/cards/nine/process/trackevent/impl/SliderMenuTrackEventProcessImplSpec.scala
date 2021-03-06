/*
 * Copyright 2017 47 Degrees, LLC. <http://www.47deg.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cards.nine.process.trackevent.impl

import cards.nine.commons.test.TaskServiceSpecification
import cards.nine.commons.test.data.trackevent.SliderMenuTrackEventTestData
import cards.nine.process.trackevent.TrackEventException
import cards.nine.services.track.{TrackServices, TrackServicesException}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope

trait SliderMenuTrackEventProcessSpecification
    extends TaskServiceSpecification
    with SliderMenuTrackEventTestData
    with Mockito {

  val trackServicesException = TrackServicesException("Irrelevant message")

  trait TrackServicesScope extends Scope {

    val mockTrackServices = mock[TrackServices]

    val process = new TrackEventProcessImpl(mockTrackServices)

  }

}

class SliderMenuTrackEventProcessImplSpec extends SliderMenuTrackEventProcessSpecification {

  "goToCollectionsByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToCollectionsByMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToCollectionsByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToCollectionsByMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToCollectionsByMenuEvent)
    }

  }

  "goToMomentsByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToMomentsByMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToMomentsByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToMomentsByMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToMomentsByMenuEvent)
    }

  }

  "goToProfileByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToProfileByMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToProfileByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToProfileByMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToProfileByMenuEvent)
    }

  }

  "goToSendUsFeedback" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToSendUsFeedback().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToSendUsFeedbackEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToSendUsFeedback().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToSendUsFeedbackEvent)
    }

  }

  "goToHelpByMenu" should {

    "track the app with the right parameters" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceRight(Unit)

      process.goToHelpByMenu().mustRightUnit

      there was one(mockTrackServices).trackEvent(goToHelpByMenuEvent)
    }

    "return a Left[TrackEventException] when the service return an exception" in new TrackServicesScope {

      mockTrackServices.trackEvent(any) returns serviceLeft(trackServicesException)

      process.goToHelpByMenu().mustLeft[TrackEventException]

      there was one(mockTrackServices).trackEvent(goToHelpByMenuEvent)
    }

  }

}
