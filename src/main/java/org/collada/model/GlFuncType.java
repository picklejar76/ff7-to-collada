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
 * <p>Java class for gl_func_type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="gl_func_type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NEVER"/>
 *     &lt;enumeration value="LESS"/>
 *     &lt;enumeration value="LEQUAL"/>
 *     &lt;enumeration value="EQUAL"/>
 *     &lt;enumeration value="GREATER"/>
 *     &lt;enumeration value="NOTEQUAL"/>
 *     &lt;enumeration value="GEQUAL"/>
 *     &lt;enumeration value="ALWAYS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "gl_func_type")
@XmlEnum
public enum GlFuncType {

    NEVER,
    LESS,
    LEQUAL,
    EQUAL,
    GREATER,
    NOTEQUAL,
    GEQUAL,
    ALWAYS;

    public String value() {
        return name();
    }

    public static GlFuncType fromValue(String v) {
        return valueOf(v);
    }

}
