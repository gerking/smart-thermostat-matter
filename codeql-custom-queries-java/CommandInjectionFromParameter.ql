/**
 * @name Shell command built from unsanitized method parameter
 * @description Demo query: treats every String parameter as potentially untrusted and
 *              tracks whether it flows into Runtime.exec(...) or a ProcessBuilder. This
 *              makes command injection spots visible even without a recognized external
 *              entry point (servlet, HTTP framework).
 * @kind path-problem
 * @problem.severity error
 * @security-severity 9.0
 * @precision high
 * @id java/demo/command-injection-from-parameter
 * @tags security
 *       external/cwe/cwe-078
 */

import java
import semmle.code.java.dataflow.DataFlow
import semmle.code.java.dataflow.TaintTracking

module CommandInjectionFromParamConfig implements DataFlow::ConfigSig {
  predicate isSource(DataFlow::Node source) {
    exists(Parameter p |
      source.asParameter() = p and
      p.getType() instanceof TypeString
    )
  }

  predicate isSink(DataFlow::Node sink) {
    exists(MethodCall ma |
      ma.getMethod().hasName("exec") and
      ma.getMethod().getDeclaringType().hasQualifiedName("java.lang", "Runtime") and
      sink.asExpr() = ma.getAnArgument()
    )
    or
    exists(ClassInstanceExpr cie |
      cie.getConstructedType().hasQualifiedName("java.lang", "ProcessBuilder") and
      sink.asExpr() = cie.getAnArgument()
    )
  }
}

module CommandInjectionFromParamFlow = TaintTracking::Global<CommandInjectionFromParamConfig>;

import CommandInjectionFromParamFlow::PathGraph

from CommandInjectionFromParamFlow::PathNode source, CommandInjectionFromParamFlow::PathNode sink
where CommandInjectionFromParamFlow::flowPath(source, sink)
select sink.getNode(), source, sink,
  "Shell command depends on unsanitized parameter $@.", source.getNode(), "here"
