package com.fortysevendeg.ninecardslauncher.services.calls.impl

import com.fortysevendeg.ninecardslauncher.services.calls.models._
import com.fortysevendeg.ninecardslauncher.services.commons._

trait CallsServicesImplData {

  val phoneHome = "666666666"
  val phoneWork = "777777777"
  val phoneMobile = "888888888"
  val phoneOther = "999999999"

  val seqPhones = Seq(phoneHome, phoneWork, phoneMobile, phoneOther)
  val seqPhoneCategory = Seq(PhoneHome, PhoneWork, PhoneMobile, PhoneOther)
  val seqCallType = Seq("IncomingType", "OutgoingType", "MissedType", "OtherType")

  val calls = generateCalls

  def generateCalls: Seq[Call] =
    0 to 3 map { i =>
      Call(
        seqPhones(i),
        Option(s"contact$i"),
        seqPhoneCategory(i),
        i.toString,
        seqCallType(i))
    }

}
