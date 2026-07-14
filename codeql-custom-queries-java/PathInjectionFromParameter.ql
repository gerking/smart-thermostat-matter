/**
 * @name File path built from unsanitized method parameter
 * @description Demo query: treats every String parameter as potentially untrusted and
 *              tracks whether it flows into the construction of a java.io.File. This
 *              makes path traversal spots visible even without a recognized external
 *              entry point.
 * @kind path-problem
 * @problem.severity error
 * @security-severity 7.5
 * @precision high
 * @id java/demo/path-injection-from-parameter
 * @tags security
 *       external/cwe/cwe-022
 */

import java
import semmle.code.java.dataflow.DataFlow
import semmle.code.java.dataflow.TaintTracking

module PathInjectionFromParamConfig implements DataFlow::ConfigSig {
  predicate isSource(DataFlow::Node source) {
    exists(Parameter p |
      source.asParameter() = p and
      p.getType() instanceof TypeString
    )
  }

  predicate isSink(DataFlow::Node sink) {
    exists(ClassInstanceExpr cie |
      cie.getConstructedType().hasQualifiedName("java.io", "File") and
      sink.asExpr() = cie.getAnArgument()
    )
  }
}

module PathInjectionFromParamFlow = TaintTracking::Global<PathInjectionFromParamConfig>;

import PathInjectionFromParamFlow::PathGraph

from PathInjectionFromParamFlow::PathNode source, PathInjectionFromParamFlow::PathNode sink
where PathInjectionFromParamFlow::flowPath(source, sink)
select sink.getNode(), source, sink,
  "File path depends on unsanitized parameter $@.", source.getNode(), "here"
