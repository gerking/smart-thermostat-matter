/**
 * @name Hardcoded secret in a constant field
 * @description Finds static final String fields whose name suggests a secret/credential
 *              (password, key, token, passcode, credential) that are initialized
 *              directly with a string literal instead of being loaded from a secret
 *              store. The keyword list is deliberately generic (no project-specific
 *              field names), so this fires on any codebase, not just this demo project.
 * @kind problem
 * @problem.severity warning
 * @security-severity 7.5
 * @precision high
 * @id java/demo/hardcoded-secret-field
 * @tags security
 *       external/cwe/cwe-798
 */

import java

from Field f, StringLiteral lit
where
  f.isStatic() and
  f.isFinal() and
  f.getType() instanceof TypeString and
  lit = f.getInitializer() and
  f.getName().toUpperCase().regexpMatch(".*(PASSWORD|PASSCODE|SECRET|TOKEN|KEY|CREDENTIAL).*")
select f, "Field '" + f.getName() + "' hardcodes a secret directly in source, instead of provisioning it securely."
