/**
 * @name SQL query built from unsanitized method parameter
 * @description Demo query: unlike the built-in java/sql-injection query, which only
 *              starts from recognized RemoteFlowSource sources (e.g. HttpServletRequest),
 *              this query treats EVERY String parameter of a method as potentially
 *              untrusted. This suits a small demo/library project without a real web
 *              layer where it should still be shown that a method is vulnerable to SQL
 *              injection as soon as it is called from outside with unchecked data.
 * @kind path-problem
 * @problem.severity error
 * @security-severity 8.6
 * @precision high
 * @id java/demo/sql-injection-from-parameter
 * @tags security
 *       external/cwe/cwe-089
 */

import java
import semmle.code.java.dataflow.DataFlow
import semmle.code.java.dataflow.TaintTracking

module SqlInjectionFromParamConfig implements DataFlow::ConfigSig {
  predicate isSource(DataFlow::Node source) {
    exists(Parameter p |
      source.asParameter() = p and
      p.getType() instanceof TypeString
    )
  }

  predicate isSink(DataFlow::Node sink) {
    exists(MethodCall ma |
      ma.getMethod().hasName(["executeQuery", "execute", "executeUpdate"]) and
      ma.getMethod().getDeclaringType().getASupertype*().hasQualifiedName("java.sql", "Statement") and
      sink.asExpr() = ma.getArgument(0)
    )
  }
}

module SqlInjectionFromParamFlow = TaintTracking::Global<SqlInjectionFromParamConfig>;

import SqlInjectionFromParamFlow::PathGraph

from SqlInjectionFromParamFlow::PathNode source, SqlInjectionFromParamFlow::PathNode sink
where SqlInjectionFromParamFlow::flowPath(source, sink)
select sink.getNode(), source, sink,
  "SQL query depends on unsanitized parameter $@.", source.getNode(), "here"
