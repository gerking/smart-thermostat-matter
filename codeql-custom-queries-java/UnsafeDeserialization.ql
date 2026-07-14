/**
 * @name Deserialization via ObjectInputStream without visible safeguards
 * @description Every call to ObjectInputStream.readObject() on a stream that doesn't
 *              obviously originate from a trusted, purely local source is a candidate
 *              for unsafe deserialization (CWE-502). This demo query flags every use of
 *              readObject() outright, since in the context of this project (receiving
 *              commands over a network connection) every occurrence is relevant.
 * @kind problem
 * @problem.severity error
 * @security-severity 8.1
 * @precision medium
 * @id java/demo/unsafe-deserialization
 * @tags security
 *       external/cwe/cwe-502
 */

import java

from MethodCall ma
where
  ma.getMethod().hasName("readObject") and
  ma.getMethod().getDeclaringType().hasQualifiedName("java.io", "ObjectInputStream")
select ma, "Deserializes data with readObject() without visible validation/whitelisting of allowed classes."
