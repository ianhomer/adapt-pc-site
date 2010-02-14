/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Copyright 2010 bemoko 
 */
package adapt 

import com.bemoko.live.platform.mwc.plugins.AbstractPlugin

/**
 * Plugin that proxies to the underlying application site.
 */
 
/*
 * We use HtmlUnit to process HTML page so we need to load these dependencies
 */
@Grab(group='net.sourceforge.htmlunit', module='htmlunit', version='2.6')
@Grab(group='net.sourceforge.htmlunit', module='htmlunit-core-js', version='2.6')
@Grab(group='commons-httpclient', module='commons-httpclient', version='3.1')
@Grab(group='net.sourceforge.cssparser', module='cssparser', version='0.9.5')

class Proxy extends AbstractPlugin {
  def contentId
  def url
  def content
      
  void initialise(Map p) {
    contentId = p.contentId
    if (!contentId) {
      throw new Exception("Content ID must be set when you initialise this plugin")
    }
    url = p.url
  }
  
  void execute(Map p) {
    
    /*
     * Create a web client which we will use to proces the page.  We'll disable CSS and
     * JavaScript to optimise the process flow
     */
    def webClient = new com.gargoylesoftware.htmlunit.WebClient()
    webClient.cssEnabled=false
    webClient.javaScriptEnabled=false
    webClient.throwExceptionOnScriptError=false
    def page
    try {
      page = getPage(p,webClient)
      
      /*
       * If we've got a page object then we can continue with the processing of it
       */
      if (page) {
        content = page.getElementById(contentId)?.asXml()
      } else {
        throw new Exception("Model not loaded")
      }
    } finally {
      /*
       * Free up resources from web client
       */
      webClient.closeAllWindows()
    }
  }
  
  def getPage(p,webClient) {
    return webClient.getPage(url)
  }
  
}