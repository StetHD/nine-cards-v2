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

package cards.nine.app.ui.commons.dialogs.widgets

import cards.nine.app.ui.commons.Jobs
import cards.nine.commons.services.TaskService.TaskService
import cards.nine.commons.services.TaskService._
import macroid.ActivityContextWrapper

class WidgetsDialogJobs(actions: WidgetsDialogUiActions)(
    implicit contextWrapper: ActivityContextWrapper)
    extends Jobs {

  def initialize(): TaskService[Unit] =
    for {
      _ <- actions.initialize()
      _ <- loadWidgets()
    } yield ()

  def loadWidgets(): TaskService[Unit] =
    for {
      _       <- actions.showLoading()
      widgets <- di.deviceProcess.getWidgets
      _       <- actions.loadWidgets(widgets)
    } yield ()

  def close(): TaskService[Unit] = actions.close()

}
