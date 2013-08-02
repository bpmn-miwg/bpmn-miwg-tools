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
	this.orig = [];
	this.init = function() {
	  var variants = ['reference','vendor'];
	  $.each(variants, function(i,d) {
	      mi.loadBpmn(d);
	      // register highlight click handlers for xpath findings
	      $('.finding span.'+d+'[data-xpath]').append('&nbsp;<a class="'+d+'">Show in '+d+'</a>').click(function(){
	    	 console.log('finding clicked'); 
	    	 mi.scrollToXPath($(this).parent().parent().parent().data('test'),$(this).parent().data('xpath'),d);
	    	 return false;
	      }); 
	      // register highlight click handlers for string findings
	      $('.finding span.'+d+'[data-frag]').append('&nbsp;<a class="'+d+'">Show in '+d+'</a>').click(function(){
	    	 console.log('finding clicked'); 
	    	 mi.scrollToFrag($(this).parent().parent().parent().data('test'),$(this).data('frag'),d);
	    	 return false;
	      }); 
	  });
  }
  this.reset = function() {
    $('#highlight-reference').removeClass('highlight');
    $('#highlight-vendor').removeClass('highlight');
    if (mi.orig.length == ($('code.reference').length*2)) {
      console.log('All models fetched, now loading...');
      for (idx in mi.orig) {
        if (mi.orig[idx].html == null) {
        	$( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target )
        	.empty().html( mi.orig[idx].data );
	        $('pre code').each(function(i, d) { 
	        	hljs.highlightBlock(d);
	        	mi.orig[idx].html = $( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target ).html();
	        	console.log('added syntax highlighting: '+ mi.orig[idx].html.length);
	        });
        } else {
        	console.log('Already applied syntax highlighting'); 
        	$( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target )
        		.empty().html( mi.orig[idx].html );
        }
      }
    } else {
      console.log('Loaded '+mi.orig.length+' of '+$('code.reference').length*2+', waiting for all models to be fetched');
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
          var o = {
        	  "target":target,
        	  "test":test,
        	  "data":data.replace(/</g,'&lt;'),
        	  "html":null,
        	  "xml":$.parseXML(data)
          };
          mi.orig.push(o);
          mi.reset();
        },
        error: function(jqXHR, textStatus, errorThrown) {
          console.error(textStatus);
        }
      });
    });
  };
  this.scrollToXPath = function(test, idxOrFrag, target) {
	  // NOTE THAT THIS DOES NOT WORK!! SUGGESTIONS WELCOMED
	  
	  // make a local copy of the first xml document loaded to play with 
	  var x = mi.orig[0].xml;
	  
	  // create a namespace resolver 
	  var nsResolver = x.createNSResolver( x );
	  
	  // both of these return root === undefined
	  //var root = x.evaluate('//semantic:definitions',x,nsResolver ,XPathResult.ANY_TYPE,null);
	  var root = x.evaluate('/',x,nsResolver ,XPathResult.ANY_TYPE,null);
	 
	  
  };
  this.scrollToFrag = function(test, idxOrFrag, target) {
	this.showActivityIndicator('Searching...');
    this.reset();    
    var frag = idxOrFrag;
    //if (!isNaN(idxOrFrag)) frag = frags[idxOrFrag];
    var refRtn = this.highlightFrag(test,frag,'reference');
    var vendorRtn = rtn = this.highlightFrag(test,frag,'vendor');
    if (target=='reference' && refRtn != -1) {
    	document.getElementById('highlight-'+target).scrollIntoView(false); 
    } else if (target=='vendor' && vendorRtn != -1) { 
    	document.getElementById('highlight-'+target).scrollIntoView(false); 
    }
    this.hideActivityIndicator();
  };
  this.highlightFrag= function(test,fragToSeek,target) {
	console.log('Highlighting: '+ fragToSeek+' in '+test+'.'+target);
	if (fragToSeek===undefined) {
		console.error('No fragment specified');
		return -1;
	}
    var el = $( 'div[data-test="'+test+'"] code.'+target ); 
    var xml = el.html();
    // adjust string to seek for the code highlighting 
    // e.g. id="11" becomes 
    //      <span class="attribute">id</span></span>=<span class="value"><span class="value">"11
    fragToSeek = fragToSeek.replace('id="', '<span class="attribute">id<\/span><\/span>=<span class="value"><span class="value">"');
    
    var idx = xml.indexOf(fragToSeek);
    if (idx==-1) {
      console.log('Frag not found: '+fragToSeek);
      $('a[data-frag="'+fragToSeek+'"] .'+target).replaceWith('<span id="highlight-'+target+'" class="highlight">Not found</span>');
    } else { 
      console.log('Found frag at: '+ idx);
      var fragBefore = xml.substring(0,idx);
      //console.log('Frag before: '+fragBefore.substring(0,20)+'...'+fragBefore.substring(fragBefore.length-20));
      var fragAfter = xml.substring(idx+fragToSeek.length);
      //console.log('Frag after: '+fragAfter.substring(0,20)+'...'+fragAfter.substring(fragBefore.length-20));
      el.html(fragBefore+'<span id="highlight-'+target+'" class="highlight">'+fragToSeek+'</span>'+fragAfter);
    }
    return idx; 
  }
  this.showActivityIndicator = function(msg) {
	  document.body.style.cursor='progress';  
	  if (msg !== undefined) msg = 'Working...';
	  $('.messages').empty().append(msg).removeClass('hide').addClass('blink');
  }
  this.hideActivityIndicator = function() {
	  $('.messages').addClass('hide');
	  document.body.style.cursor='auto';  
  }
}