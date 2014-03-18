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

//var baseUrl = 'http://tools.bpmn-miwg.cloudbees.net';
var baseUrl = '.';
var mi = new ModelInterchangePresenter();
$(document).ready(function() {
    console.log('ready');
    mi.init();
});

var classifiers = [ new ExprLangClassifier(), new SchemaLocationClassifier() ];

function ModelInterchangePresenter() {
    this.orig = [];
    this.init = function() {
      console.log('Initialising ModelInterchangePresenter...');
      
      mi.results = [];
      if (window.location.href.indexOf('overview')!=-1) { // overview page 
          $('.testresults').addClass('hide');
          var container = $('body').append('<div class="mi-table container-fluid">'
                  +'<div class="mi-header row-fluid">'
                    +'<h2 class="offset1 span6">Test</h2>'
                    +'<h2 class="span2">Assertions (findings/total)</h2>'
                    +'<h2 class="span2">Differences detected</h2>'
                  +'</div>');
          $('.test > a').sort().each(function(i,d) {
              var s = d.href.substring(d.href.lastIndexOf('/')+1).split('-');
              // workaround for itp-commerce and camunda-bpmn
              if (s.length>3) {
                  console.log('workaround: s='+s.length);
                  s[0]=s[0]+'-'+s[1];
                  s[1]=s[2];
                  s[2]=s[3];
              }
              console.log('vendor'+s[0]+'s2: '+ s[2]);
              var cur = {
                suite: s[1],
                vendor: unescape(s[0]),
                /* Reference has no variant, e.g. "Reference-A.1.0.bpmn.txt" */
                  variant: s[2] === undefined ? '' : s[2].substring(0,s[2].indexOf('.')),
                  url: d.href,
                  xpathUrl: baseUrl+'/xpath/'+d.href.substring(d.href.lastIndexOf('/')+1),
                  xmlCompareUrl: baseUrl+'/xml-compare/'+d.href.substring(d.href.lastIndexOf('/')+1,d.href.length-9)+'.html',
                  findings: $(d).parent().data('findings') === undefined ? 'N/A' : $(d).parent().data('findings'),
                  ok: $(d).parent().data('ok') === undefined ? 'N/A' : $(d).parent().data('ok'),
                  diffs: $(d).parent().data('diffs') === undefined ? 'N/A' : $(d).parent().data('diffs')
              };
              var prev = mi.results[i-1];
              if (prev===undefined || prev.vendor!=cur.vendor) {
                  container.append('<div class="row-fluid"><h3 class="offset1 span10" id="'+cur.vendor+'">'+cur.vendor+'</h3></div>');
              }
              var findingsClass = cur.findings == 0 ? 'mi-no-findings' : 'mi-findings';
              var diffsClass = cur.diffs == 0 ? 'mi-no-findings' : 'mi-findings';
              container.append('<div class="row-fluid">'
                      +'<span class="mi-test offset1 span6">'
                      +cur.suite+': '+cur.variant
                      +'</span>'
                      +'<span class="span2 '+findingsClass+'"><a href="'+cur.xpathUrl+'">'+cur.findings+'/'+(cur.findings+cur.ok)+'</a></span>'
                      +'<span class="span2 '+diffsClass+'"><a href="'+cur.xmlCompareUrl+'">'+cur.diffs+'</span>'
                      +'</div>');
              mi.results.push(cur);
          });
      } else {
        // individual test results page
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
          
          // TODO Ideally Sven could fix this at his end
          $('[data-xpath]').each(function(i,d) {
            $(d).data('xpath', mi.fixXPath($(d).data('xpath')));
            console.log('check modification set: '+ $(d).data('xpath'));
          });
          
          mi.classifyFindings();
          // number findings MUST occur before we add the span6 bpmn containers
          $('.test').wrapInner('<ol>');
          $('.finding h4').wrapInner('<li>');
          
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
              $('.finding span.'+d+'[data-xpath]').append('&nbsp;<a class="'+d+'">Show</a>').click(function(){
                 console.log("clicked 'finding'"); 
                 mi.scrollToXPath($(this).parent().parent().parent().parent().data('test'),$(this).data('xpath'),d);
                 return false;
              });
              
              // register highlight click handlers for string findings
              $('.finding span.'+d+'[data-frag]').append('&nbsp;<a class="'+d+'">Show</a>').click(function(){
                 console.log("clicked 'finding'"); 
                 mi.scrollToFrag($(this).parent().parent().parent().parent().data('test'),$(this).data('frag'),d);
                 return false;
              }); 
          });
          
    } // END else render individual results page
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
  /**
   * @param d xml-compare finding 
   */
  this.classifyFindings = function() {
    $('.finding > h4').each(function(i,d) {
      var indicator = $(d).prepend('<span class="mi-unknown-finding img-rounded"></span>');
    
      $.each(classifiers, function(i2,d2) {
          console.log('i:'+i2+',d:'+d2+'p:'+$(d).parent()+',o:'+indicator); 
          d2.classify($(d).parent());
      });
    });
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
              "data":data.replace(/</g,'&lt;'),
              "file":file,
              "html":null,
              "target":target,
            "test":test,
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
  this.fixXPath = function(xpath) {
      // fix xpath expressions provided without namespace 
      // (TODO assumption of semantic is not always right)
      xpath = xpath.replace(/\/(?!@)/g,'/semantic:');
      console.log('modified xpath to: '+xpath);
      // cut off any attributes and look instead for the parent element
      xpath = xpath.replace(/\/@.*$/g,'')
      console.log('modified xpath to: '+xpath);
      return xpath; 
  };
  this.loadBpmn = function() {
      $.each(mi.orig, function(idx,d) {
        if (mi.orig[idx].html==null) {
          $( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target )
        .empty().html( mi.orig[idx].data );
        $('pre code[data-file="'+mi.orig[idx].file+'"]').each(function(i, d) { 
          hljs.highlightBlock(d);
          mi.orig[idx].html = $( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target ).html();
          console.log('added syntax highlighting for idx: '+idx+': '+ mi.orig[idx].html.length);
          mi.orig[idx].htmlInited = true; 
        });
        } else {
          console.log('Already applied syntax highlighting'); 
        $( 'div[data-test="'+mi.orig[idx].test+'"] code.'+mi.orig[idx].target )
            .empty().html( mi.orig[idx].html );
        }
      });
  }
  this.scrollToXPath = function(test, xpath, target) {
      //alert('Sorry at the moment we cannot scroll to an xpath fragment');
    console.log('Seeking xpath: '+xpath+' in test: '+test);
      
      // make a local copy of the xml document loaded to play with 
    $.each(mi.orig, function(i,d) {
      if (d.test==test) {
        var x = mi.orig[i].xml;
        
          // create a namespace resolver 
          var nsResolver = x.createNSResolver( x );
    
          var root = x.evaluate(xpath,x,nsResolver,XPathResult.ANY_TYPE,null);
          
          var id = root.iterateNext().getAttribute('id');
          mi.scrollToFrag(test, id, target);
      } else { 
        console.debug('Skipping test: '+ d.test);
      }
    });
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

var tmp ; 
function ExprLangClassifier() {
    this.classify = function(d) {
        $('.vendor:contains("expressionLanguage")').parent().siblings('h4')
            .children('.mi-unknown-finding')
            .removeClass('mi-unknown-finding')
            .addClass('mi-equivalent-finding')
    }
}
function SchemaLocationClassifier() {
    this.classify = function(d) {
        $('.vendor:contains("http://www.omg.org/spec/BPMN/20100524/MODEL http://bpmn.sourceforge.net/schemas/BPMN20.xsd")')
            .parent().siblings('h4')
            .children('.mi-unknown-finding')
            .removeClass('mi-unknown-finding')
            .addClass('mi-equivalent-finding')
    }
}
