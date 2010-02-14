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

class ProxyAsWebService extends Proxy {
  private static DEFAULT_ORIGINATING_URL=new URL("http://bemoko.com/")
  private static pageCreator=new com.gargoylesoftware.htmlunit.DefaultPageCreator()

  /*
   * If non-mobile status code is non-zero, then if a non-mobile device connects
   * then the given status code will be returned without any page processing.  This
   * provides a convenient mechanism for a web service client to identify that
   * content need not be transformed and can be delivered as is
   */
  int nonMobilStatusCode = 0
  
  void initialise(Map p) {
    if (p.nonMobilStatusCode) {
      nonMobilStatusCode = p.nonMobilStatusCode
    }
    super.initialise(p)
  }

  void execute(Map p) {
    log.info(p)
    if (nonMobilStatusCode > 0 && user.device.isMobile == 'false') {
      /*
       * For non-mobile devices we return a 204 so the calling process knows that it can 
       * deal with the response as a PC response
       */
      log.info("Not mobile")        
      platform.response.status = 204
      return
    }

    super.execute(p)
  }

  def getPage(p,webClient) {
    if (!p.markup) {
      throw new Exception("No markup parameter specified")
    }
    log.info("Markup = ${p.markup}")
    /*
     * Generate the page from HTML sent in as intent parameter, 
     * e.g. form post or api call
     */
    def window=new com.gargoylesoftware.htmlunit.TopLevelWindow("top",webClient)  
    def webResponse=new
      com.gargoylesoftware.htmlunit.StringWebResponse(p.markup,DEFAULT_ORIGINATING_URL) 
    return pageCreator.createPage(webResponse,window)
  }
}