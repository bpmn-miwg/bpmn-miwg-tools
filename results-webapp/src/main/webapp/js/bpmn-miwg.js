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
$(document).ready(function() {
	console.log('ready');
	mi.init();
});

function ModelInterchangePresenter() {
	this.orig = [];
	this.init = function() {
      console.log('Initialising ModelInterchangePresenter...');
      
      mi.results = [];
      if (window.location.href.indexOf('overview')!=-1) { // overview page 
    	  $('.testresults').addClass('hide');
    	  $('.test > a').each(function(i,d) {
    		  var s = d.href.substring(d.href.lastIndexOf('/')+1).split('-');
    		  var cur = {suite: s[1],vendor:unescape(s[0]),variant:s[2],url:d.href};
    		  var prev = mi.results[i-1];
    		  if (prev===undefined || prev.vendor!=cur.vendor) {
    			  $('body').append('<h2>'+cur.vendor+'</h2>');
    		  }
    		  $('body').append('<a href="'+cur.url+'">Test '+cur.suite+': '+cur.variant+'</a><br/>');
    		  mi.results.push(cur);
    	  });
      } else {
	      // enhance basic data with presentation classes
	      $('.testresults').addClass('container');
	      $('.test').addClass('well').addClass('span12');
	      
	      // add additional presentation structures
	      $('body').append('<div class="hide messages"></div>');
	      $('.testresults').prepend('<a name="top"></a>');
	      $('body').append(
	    		  '<div class="backToFindings"><a href="#top">Top</a></div>'); 
	      
	      $($('.detailedoutput .reference')).prepend('<label>Reference:</label>');
	      $($('.detailedoutput .vendor')).prepend('<label>Vendor:</label>');
	      
	      var variant = 'seriously-cant-we-get-the-variant-more-easily-than-this';
	      if (window.location.href.indexOf('export')!=-1) {
	    	  variant = 'export';
	      } else if (window.location.href.indexOf('roundtrip')!=-1) {
	    	  variant = 'roundtrip';
	      } else if (window.location.href.indexOf('import')!=-1) {
	    	  variant = 'import';
	      } else { 
	    	  console.error('This is an unexpected test result file. Talk to the Tools team.');
	      }
	      console.log('detected test variant to be:'+variant); 
	      
	      $('.test').each(function(i,d) {
	    	  var test = $(d).data('test');
	    	  var referenceFile = '/'+test.substring(0,1)+'/Reference/'+test;
	    	  $(d).append('<div class="span6"><h4>Reference</h4><pre><code class="xml reference" data-file="'+referenceFile+'"></code></pre></div>');
	      
	    	  var vendor = $(d).parent().data('vendor');
	    	  var vendorFile = '/'+test.substring(0,1)+'/'+vendor+'/'+test.substring(0,test.indexOf('.bpmn'))+'-'+variant+'.bpmn';
	    	  $(d).append('<div class="span6"><h4>'+vendor+'</h4><pre><code class="xml vendor" data-file="'+vendorFile+'"></code></pre></div>');
	    
	      });
	      
	      // load bpmn and enhance with links etc.
		  var variants = ['reference','vendor'];
		  $.each(variants, function(i,d) {
		      mi.fetchBpmn(d);
		      // register highlight click handlers for xpath findings
		      /* TODO disabled this until we can get the scrollToXPath working
		      $('.finding span.'+d+'[data-xpath]').append('&nbsp;<a class="'+d+'">Show in '+d+'</a>').click(function(){
		    	 console.log("clicked 'finding'"); 
		    	 mi.scrollToXPath($(this).parent().parent().parent().data('test'),$(this).parent().data('xpath'),d);
		    	 return false;
		      });
		      */ 
		      // register highlight click handlers for string findings
		      $('.finding span.'+d+'[data-frag]').append('&nbsp;<a class="'+d+'">Show in '+d+'</a>').click(function(){
		    	 console.log("clicked 'finding'"); 
		    	 mi.scrollToFrag($(this).parent().parent().parent().data('test'),$(this).data('frag'),d);
		    	 return false;
		      }); 
		  });
      } // else render individual results page
  }
  this.reset = function() {
    $('#highlight-reference').removeClass('highlight');
    $('#highlight-vendor').removeClass('highlight');
    if (mi.orig.length == ($('code.reference').length*2)) {
      console.log('All models fetched, now loading...');
      mi.loadBpmn();
    } else {
      console.log('Loaded '+mi.orig.length+' of '+$('code.reference').length*2+', waiting for all models to be fetched');
    }
  };
  this.fetchBpmn = function(target) {
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
          console.error(textStatus+':'+errorThrown);
          $('.messages').append('<b>'+textStatus+':</b> Unable to load file: '+file+' for test: '+test
        		  +'<button class="btn" onclick="mi.loadBpmn();$(\'.messages\').hide();">Dismiss</button>').show();
        }
      });
    });
  };
  this.loadBpmn = function() {
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
  }
  this.scrollToXPath = function(test, idxOrFrag, target) {
	  alert('Sorry at the moment we cannot scroll to an xpath fragment');
	  // NOTE THAT THIS DOES NOT WORK!! SUGGESTIONS WELCOMED
	  
	  // make a local copy of the first xml document loaded to play with 
	  /*var x = mi.orig[0].xml;
	  
	  // create a namespace resolver 
	  var nsResolver = x.createNSResolver( x );
	  
	  // both of these return root === undefined
	  //var root = x.evaluate('//semantic:definitions',x,nsResolver ,XPathResult.ANY_TYPE,null);
	  var root = x.evaluate('/',x,nsResolver ,XPathResult.ANY_TYPE,null);
	  
	  THIS LOOKS PROMISING, finds the node, but how to highlight?
	  mi.orig[0].xml.evaluate('/definitions[1]/BPMNDiagram[1]/BPMNPlane[1]/BPMNShape[1]', mi.orig[1].xml, null, XPathResult.ANY_TYPE,null);
	  */
	  
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