package au.com.dius.pact.model.unfiltered

import java.io.{StringReader, ByteArrayInputStream}

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import unfiltered.netty.ReceivedMessage
import unfiltered.request.HttpRequest
import org.specs2.mock.Mockito

@RunWith(classOf[JUnitRunner])
class ConversionsTest extends Specification with Mockito {
  isolated

  var request = mock[HttpRequest[ReceivedMessage]]

  request.headerNames returns List("Accept").iterator
  request.headers("Accept") returns List("application/json").iterator
  request.headers("Content-Encoding") returns List().iterator
  request.reader returns new StringReader("")

  "converting an unfiltered request to a pact request" should {

    "construct the pact request correctly" should {

      "with a query string" in {
        request.parameterNames returns List("a", "b").iterator
        request.parameterValues("a") returns Seq("1")
        request.parameterValues("b") returns Seq("2")
        request.uri returns "/path?a=1&b=2"

        val pactRequest = Conversions.unfilteredRequestToPactRequest(request)
        pactRequest.path must beEqualTo("/path")
        pactRequest.query must beEqualTo(Some("a=1&b=2"))
      }

      "with no query string" in {
        request.parameterNames returns List().iterator
        request.uri returns "/path"

        val pactRequest = Conversions.unfilteredRequestToPactRequest(request)
        pactRequest.path must beEqualTo("/path")
        pactRequest.query must beEqualTo(None)
      }

      "with a path ending with a question mark" in {
        request.parameterNames returns List().iterator
        request.uri returns "/path?"

        val pactRequest = Conversions.unfilteredRequestToPactRequest(request)
        pactRequest.path must beEqualTo("/path")
        pactRequest.query must beEqualTo(None)
      }

    }

  }

}
