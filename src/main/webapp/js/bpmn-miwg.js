/*
 * The MIT License (MIT)
 * Copyright (c) 2013 OMG BPMN Model Interchange Working Group
 *
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

var mi = new ModelInterchangePresenter();

function ModelInterchangePresenter() {
  this.reset = function() {
    $('#highlight-reference').removeClass('highlight');
    $('#highlight-vendor').removeClass('highlight');
    if (orig.length == ($('code.reference').length*2)) {
      for (idx in orig) {
        $( 'div[data-test="'+orig[idx].test+'"] code.'+orig[idx].target ).empty().html( orig[idx].data );
        $('pre code').each(function(i, e) { hljs.highlightBlock(e) });
      }
    } else {
      console.log('waiting for all models to be loaded');
    }
  };
  this.loadBpmn = function(target) {
    $.each($('code.'+target), function(i,d) {
      var test = $(d).parent().parent().parent().data('test');
      var file = $(d).data('file');
      console.log('loading: '+file+' for '+target+', '+test);
      $.ajax({
        url: file,
        dataType: 'text',
        success: function( data ) {
          console.log('success loading '+file+' for '+test);
          var xml = data.replace(/</g,'&lt;');
          orig.push({"target":target,"test":test,"data":xml});
          mi.reset();
        },
        error: function(jqXHR, textStatus, errorThrown) {
          console.error(textStatus);
        }
      });
    });
  };
  this.scrollToFrag = function(idx, target) {
    this.reset();    
    this.highlightFrag(idx,'reference');
    this.highlightFrag(idx,'vendor');
    document.getElementById('highlight-'+target).scrollIntoView(false); 
  };
  this.highlightFrag= function(fragIdx,target) {
    var fragToSeek = frags[fragIdx];
    console.log('Highlighting: '+ fragToSeek+' in '+target);
    var xml = $('#'+target).html();
    // adjust string to seek for the code highlighting 
    // e.g. id="11" becomes 
    //      <span class="attribute">id</span></span>=<span class="value"><span class="value">"11
    fragToSeek = fragToSeek.replace('id="', '<span class="attribute">id<\/span><\/span>=<span class="value"><span class="value">"');
    
    var idx = xml.indexOf(fragToSeek);
    if (idx==-1) {
      $('li[data-index='+fragIdx+'] .'+target).replaceWith('<span id="highlight-'+target+'" class="highlight">Not found</span>');
    } else { 
      console.log('Found frag at: '+ idx);
      var fragBefore = xml.substring(0,idx);
      //console.log('Frag before: '+fragBefore.substring(0,20)+'...'+fragBefore.substring(fragBefore.length-20));
      var fragAfter = xml.substring(idx+fragToSeek.length);
      //console.log('Frag after: '+fragAfter.substring(0,20)+'...'+fragAfter.substring(fragBefore.length-20));
      $( '#'+target ).html(fragBefore+'<span id="highlight-'+target+'" class="highlight">'+fragToSeek+'</span>'+fragAfter);
    }
  }
}