/**
 * The MIT License (MIT)
 * Copyright (c) 2013 Signavio, OMG BPMN Model Interchange Working Group
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

package org.omg.bpmn.miwg.xmlCompare.util.xml.diff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.omg.bpmn.miwg.xmlCompare.util.ArrayUtil;
import org.omg.bpmn.miwg.xmlCompare.util.ObjectUtils;



/**
 * Encapsulates configuration settings for SVG comparison.
 * 
 * Most fields are arrays of strings representing XPaths that match the target nodes or attributes.
 * 
 * @author philipp.maschke
 *
 */
public class XmlDiffConfiguration {
	
	private List<String>			ignoredNodes;
	private List<String>			ignoredAttributes;
	private List<String>			optionalAttributes;
	private List<String>			caseInsensitiveAttributes;
	private List<String>			idsAndIdRefs;
	private List<String>			ignoredAttributeNamespaces;
	private List<String>			languageSpecificAttributes;
	private ElementsPrefixMatcher prefixMatcher;
	private Map<String, Pattern>	defaultAttributeValues;
	
	public XmlDiffConfiguration(String[] ignoredNodes, String[] ignoredAttributes, String[] optionalAttributes, 
			String[] idsAndIdRefs, String[] ignoredNamespacesInAttributes,
			String[] caseInsensitiveAttributes, String[] languageSpecificAttributes,
			ElementsPrefixMatcher prefixMatcher, String[][] defaultAttributeValues) {
		this.ignoredNodes = ArrayUtil.toMutableList(ignoredNodes);
		this.ignoredAttributes = ArrayUtil.toMutableList(ignoredAttributes);
		this.optionalAttributes = ArrayUtil.toMutableList(optionalAttributes);
		this.idsAndIdRefs = ArrayUtil.toMutableList(idsAndIdRefs);
		this.ignoredAttributeNamespaces = ArrayUtil.toMutableList(ignoredNamespacesInAttributes);
		this.caseInsensitiveAttributes = ArrayUtil.toMutableList(caseInsensitiveAttributes);
		this.languageSpecificAttributes = ArrayUtil.toMutableList(languageSpecificAttributes);
		this.prefixMatcher = prefixMatcher;
		this.defaultAttributeValues = new HashMap<String, Pattern>();
		if (defaultAttributeValues != null) {
			for (String[] pair : defaultAttributeValues) {
				
				ObjectUtils.checkNonNull(pair[0], pair[1]);
				this.defaultAttributeValues.put(pair[0], Pattern.compile(pair[1]));
			}
		}
	}
	
	
	/**
	 * An array of XPath strings matching nodes. Differences originating in matching nodes or their children/attributes
	 * will be ignored.
	 * 
	 * @return the <b>live</b> list of ignored nodes (changing it will change the internal copy!)
	 */
	public List<String> getIgnoredNodes() {
		return ignoredNodes;
	}
	
	
	/**
	 * An array of XPath strings matching attributes. All differences concerning matching attributes will be ignored.
	 * 
	 * @return the <b>live</b> list of ignored attributes (changing it will change the internal copy!)
	 */
	public List<String> getIgnoredAttributes() {
		return ignoredAttributes;
	}
	
	
	/**
	 * An array of XPath strings matching attributes. Matching attributes may be missing without causing differences,
	 * but differing content will not be ignored.
	 * 
	 * @return the <b>live</b> list of optional attributes (changing it will change the internal copy!)
	 */
	public List<String> getOptionalAttributes() {
		return optionalAttributes;
	}
	
	
	/**
	 * Array of simple attribute names (no XPath, no '@' signs) for attributes that may contain ids and id references.
	 * Needed to ignore differences caused by differing assignment of ids.
	 * 
	 * @return the <b>live</b> list of id or idref attributes (changing it will change the internal copy!)
	 */
	public List<String> getIdsAndIdRefNames() {
		return idsAndIdRefs;
	}
	
	
	/**
	 * Array of URI strings. All differences caused by attributes belonging to matching namespaces will be ignored.
	 * 
	 * @return the <b>live</b> list of ignored attribute namespaces (changing it will change the internal copy!)
	 */
	public List<String> getIgnoredNamespacesInAttributes() {
		return ignoredAttributeNamespaces;
	}
	
	
	/**
	 * Array of simple attribute names (no XPath, no '@' signs) for attributes which are case insensitive (differences
	 * in case will be ignored)
	 * 
	 * @return the <b>live</b> list of case insensitive attributes (changing it will change the internal copy!)
	 */
	public List<String> getCaseInsensitiveAttributeNames() {
		return caseInsensitiveAttributes;
	}
	
	
	/**
	 * Array of simple attribute names (no XPath, no '@' signs) for attributes whose values change depending on the
	 * user's language setting.
	 * As the SVG renderer currently does not support different languages, differences in content will be ignored.
	 * 
	 * @return the <b>live</b> list of language specific attributes (changing it will change the internal copy!)
	 */
	public List<String> getLanguageSpecificAttributes() {
		return languageSpecificAttributes;
	}
	
	/**
	 * Used for determining the namespace prefix of elements used in above XPath expressions.
	 * <b>All elements referenced in an XPath without declaring a namespace prefix need to be matched to a prefix using this matcher!</b>
	 * @return
	 */
	public ElementsPrefixMatcher getElementsPrefixMatcher(){
		return (prefixMatcher == null)? new ElementsPrefixMatcher() : prefixMatcher;
	}
	

	/**
	 * A mapping from attribute names to their default values.
	 * 
	 * @return the <b>live</b> map of default attribute values (changing it will change the internal copy!)
	 */
	public Map<String, Pattern> getDefaultAttributeValues() {
		return defaultAttributeValues;
	}
}
