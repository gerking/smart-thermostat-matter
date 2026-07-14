/**
 * @name Sensitive credential logged in cleartext
 * @description Finds logging/print calls whose arguments reference a variable or field
 *              whose name suggests a secret (key, token, password, passcode). Such
 *              values should never end up in cleartext log output.
 * @kind problem
 * @problem.severity error
 * @security-severity 6.5
 * @precision high
 * @id java/demo/sensitive-data-logged
 * @tags security
 *       external/cwe/cwe-532
 */

import java

predicate isSensitiveField(Field f) {
  f.getName().toUpperCase().regexpMatch(".*(KEY|SECRET|TOKEN|PASSWORD|PASSCODE|CREDENTIAL).*")
}

from MethodCall call, Expr arg, FieldAccess fa
where
  call.getMethod().hasName(["info", "warning", "severe", "fine", "println", "print"]) and
  arg = call.getAnArgument() and
  (fa = arg or fa.getParent+() = arg) and
  isSensitiveField(fa.getField())
select call,
  "Logs the sensitive field '" + fa.getField().getName() + "' in cleartext."
