//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.01 at 05:40:54 PM CST 
//


package org.collada.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fx_sampler_wrap_common.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="fx_sampler_wrap_common">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="NONE"/>
 *     &lt;enumeration value="WRAP"/>
 *     &lt;enumeration value="MIRROR"/>
 *     &lt;enumeration value="CLAMP"/>
 *     &lt;enumeration value="BORDER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "fx_sampler_wrap_common")
@XmlEnum
public enum FxSamplerWrapCommon {

    NONE,
    WRAP,
    MIRROR,
    CLAMP,
    BORDER;

    public String value() {
        return name();
    }

    public static FxSamplerWrapCommon fromValue(String v) {
        return valueOf(v);
    }

}
