

/*
 * reserved comment block
 * DO NOT REMOVE OR ALTER!
 */
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: Plus.java,v 1.2.4.1 2005/09/14 21:31:43 jeffsuttor Exp $
 */
package jaxp.sun.org.apache.xpath.internal.operations;

import jaxp.sun.org.apache.xpath.internal.XPathContext;
import jaxp.sun.org.apache.xpath.internal.objects.XNumber;
import jaxp.sun.org.apache.xpath.internal.objects.XObject;
import jaxp.xml.transform.TransformerException;

/**
 * The '+' operation expression executer.
 */
public class Plus extends Operation
{
    static final long serialVersionUID = -4492072861616504256L;

  /**
   * Apply the operation to two operands, and return the result.
   *
   *
   * @param left non-null reference to the evaluated left operand.
   * @param right non-null reference to the evaluated right operand.
   *
   * @return non-null reference to the XObject that represents the result of the operation.
   *
   * @throws TransformerException
   */
  public XObject operate(XObject left, XObject right)
          throws TransformerException
  {
    return new XNumber(left.num() + right.num());
  }

  /**
   * Evaluate this operation directly to a double.
   *
   * @param xctxt The runtime execution context.
   *
   * @return The result of the operation as a double.
   *
   * @throws TransformerException
   */
  public double num(XPathContext xctxt)
          throws TransformerException
  {

    return (m_right.num(xctxt) + m_left.num(xctxt));
  }

}
