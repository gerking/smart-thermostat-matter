/**
 * @name TrustManager accepts any certificate (no validation)
 * @description An X509TrustManager implementation whose checkServerTrusted(...) never
 *              throws an exception effectively accepts every server certificate,
 *              completely bypassing TLS certificate validation.
 * @kind problem
 * @problem.severity error
 * @security-severity 7.4
 * @precision high
 * @id java/demo/trust-all-manager
 * @tags security
 *       external/cwe/cwe-295
 */

import java

from Method m, Class c
where
  c.getASupertype*().hasQualifiedName("javax.net.ssl", "X509TrustManager") and
  m.getDeclaringType() = c and
  m.hasName("checkServerTrusted") and
  not exists(ThrowStmt t | t.getEnclosingCallable() = m)
select m, "checkServerTrusted() never throws, meaning it accepts every certificate (trust-all)."
