import groovy.json.JsonSlurper
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.ApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Shared
import spock.lang.Specification

class RenderSpec extends Specification {
  @Shared ApplicationUnderTest appUnderTest = new GroovyRatpackMainApplicationUnderTest()
  @Delegate TestHttpClient testClient = appUnderTest.httpClient

  def "01 - can render a String"() {
    expect:
    getText() == "Hello Greach!"

    /*
    Hint:
    Take a look at `ratpack.handling.Context#render(object)`
    */
  }

  def "02 - can render a Groovy Markup Template"() {
    expect:
    getText("welcome") == "<!DOCTYPE html><html><body><p>Hello Greach!</p></body></html>"

    /*
    Hint:
    Take a look at `ratpack.groovy.template.MarkupTemplateModule`
    */
  }

  def "03 - can render a Book as Json"() {
    given:
    requestSpec { req ->
      req.body.type("application/json")
    }

    when:
    get("api/book/1")

    then:
    def book = new JsonSlurper().parseText(response.body.text)
    with(book) {
      isbn == "1"
      quantity == 10
      price == 15.99
      title == "Ratpack Web Framework"
      author == "Dan Woods"
      publisher == "O'Reilly"
    }

    and:
    response.headers['content-type'] == 'application/json'

    /*
    Hint:
    Take a look at `ratpack.groovy.render.GroovyRendererSupport`

    The Jackson module (`ratpack.jackson.Jackson`) has a support for rendering as JSON
    */
  }

  def "04 - can render a Book as Xml"() {
    given:
    requestSpec { req ->
      req.headers.set("Accept", "application/xml")
    }

    when:
    get("api/book/1")

    then:
    def book = new XmlSlurper().parseText(response.body.text)
    with(book) {
      isbn == "1"
      quantity == 10
      price == 15.99
      title == "Ratpack Web Framework"
      author == "Dan Woods"
      publisher == "O'Reilly"
    }

    and:
    response.headers['content-type'] == 'application/xml'

    /*
    Hint:
    Take a look at `ratpack.groovy.Groovy#markupBuilder(contentType, encoding, closure)`

    Different content types can be rendered using the same renderer using `ratpack.groovy.handling.GroovyContext#byContent(closure)`
    */
  }

}
