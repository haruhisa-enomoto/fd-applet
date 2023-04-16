/* eslint-disable @typescript-eslint/no-non-null-assertion */
import { GraphData } from "react-vis-graph-wrapper";

export type SlimQuiverData = {
  vertices: string[];
  arrows: { label?: string; from: string; to: string; isTau?: boolean }[];
};

type SlimRelationData = {
  monoRelations: string[][];
  biRelations: { first: string[]; second: string[] }[];
};

export type SlimAlgebraData = {
  quiver: SlimQuiverData;
  monoRelations: string[][];
  biRelations: { first: string[]; second: string[] }[];
};

export function slim(graph: GraphData): SlimQuiverData {
  return {
    vertices: graph.nodes.map((node) => node.label!),
    arrows: graph.edges.map((edge) => ({
      label: edge.label!,
      from: edge.from!.toString(),
      to: edge.to!.toString(),
    })),
  };
}

export function fatten(quiver: SlimQuiverData): GraphData {
  return {
    nodes: quiver.vertices.map((str) => ({
      id: str,
      label: str,
      title: str,
    })),
    edges: quiver.arrows.map((arrow) => ({
      label: arrow.label,
      from: arrow.from,
      to: arrow.to,
      dashes: arrow.isTau,
    })),
  };
}

/**
 * "a *b*c, d * e, f*g - h * i,," =>
 * { monomials: ["a", "b", "c"], ["d", "e"], ["f"]],
 *  binomials: [{ first: ["f", "g"], second: ["h", "i"]}]
 */
export function stringToRelationData(rels: string): SlimRelationData {
  function isBinomial(rel: string): boolean {
    return rel.includes("-");
  }
  function monomialToArray(rel: string): string[] {
    // "a * b cd * e" => ["a", "b", "c", "d", "e"]
    return rel.replace(/[\s*]+/g, "").split("");
  }
  const relStrings = rels.split(",");
  const monomials: string[][] = [];
  const binomials: { first: string[]; second: string[] }[] = [];
  for (const rel of relStrings) {
    if (!isBinomial(rel)) {
      const result = monomialToArray(rel);
      if (result.length !== 0) monomials.push(result);
    } else {
      const paths = rel.split("-");
      if (paths.length !== 2) throw Error("Invalid commutative relations.");
      binomials.push({
        first: monomialToArray(paths[0]),
        second: monomialToArray(paths[1]),
      });
    }
  }
  return { monoRelations: monomials, biRelations: binomials };
}

export function relationDataToString(rels: SlimRelationData): string {
  const monomialPart = rels.monoRelations.map((path) => path.join("*"));
  const binomialPart = rels.biRelations.map(
    (binom) => binom.first.join("*") + " - " + binom.second.join("*")
  );
  return monomialPart.concat(binomialPart).join(", ");
}
