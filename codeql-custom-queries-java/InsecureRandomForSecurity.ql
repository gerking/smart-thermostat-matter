/**
 * @name Use of java.util.Random instead of SecureRandom
 * @description java.util.Random is not cryptographically secure. This query flags EVERY
 *              instantiation of java.util.Random in the project, regardless of class or
 *              method name - deliberately without a project-specific naming heuristic (an
 *              earlier version filtered on class names like "Commission"/"Pairing", which
 *              in practice was tailored only to this demo project). This makes the query
 *              generally applicable, at the cost of lower precision: not every use of
 *              Random is security-relevant (e.g. randomized test data, game logic).
 *              Whether a given finding actually matters needs manual review.
 * @kind problem
 * @problem.severity recommendation
 * @security-severity 5.3
 * @precision low
 * @id java/demo/insecure-random-for-security
 * @tags security
 *       external/cwe/cwe-338
 */

import java

from FieldDeclaration fd, ClassInstanceExpr cie
where fd.getAField().getInitializer() = cie 
  and cie.getType().getName() = "Random" 
  and cie.getType().getCompilationUnit().getPackage().getName() = 	"java.util" 
select fd,
  "Uses java.util.Random; if the generated value is security-relevant, java.security.SecureRandom should be used instead."

