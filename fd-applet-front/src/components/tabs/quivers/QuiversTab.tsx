import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import QuiverAccordion from "../../common/QuiverAccordion";
import UpdateButton from "../../common/UpdateButton";
import MyTabProps from "../MyTabProps";

import PairHasseAccordion from "./PairHasseAccordion";
import SubcatHasseAccordion from "./SubcatHasseAccordion";

export const HassePhysicsOption = {
  solver: "forceAtlas2Based",
  forceAtlas2Based: {
    // theta: 0.5,
    // springConstant: 0.05,
    // springLength: 200,
    gravitationalConstant: -100,
  },
};

export default function QuiversTab({ isComputed, setIsComputed }: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected } = useSelection();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const handleSelectNode = (params: any) => {
    const chosenString: string = params.nodes[0];
    const chosenStringList = chosenString
      .replaceAll(" ", "")
      .split("],[")[0]
      .replaceAll(/[[\]]+/g, "")
      .split(",")
      .map((str) => str.trim())
      .filter(Boolean);
    setSelected(chosenStringList);
  };

  async function makeRfAlgebra() {
    const response = await fetchWithUiFeedback({
      url: "/api/rf-algebra",
      expectJson: false,
    });
    if (!response.success) return;
    setIsComputed(true);
    return;
  }

  return (
    <>
      {!isComputed ? (
        <UpdateButton onClick={makeRfAlgebra} />
      ) : (
        <>
          <QuiverAccordion
            header="Syzygy quiver"
            description={
              "Vertices: Indecs\nArrows: X → (direct summands of) ΩX"
            }
            url="/api/quiver/syzygy"
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <QuiverAccordion
            header="Cosyzygy quiver"
            description={
              "Vertices: Indecs\nArrows: X → (direct summands of) ΣX"
            }
            url="/api/quiver/cosyzygy"
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <QuiverAccordion
            header="Support tau-tilting quiver"
            description={
              "Vertices: τ-tilting pairs (with support = set of vertices)\nArrows: Hasse or mutation arrow\nLabel: brick labeling"
            }
            url="/api/quiver/s-tau-tilt"
            physicOption={HassePhysicsOption}
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <QuiverAccordion
            header="Generalized tilting quiver"
            description={
              "Vertices: Generalized tilting modules (with pd < ∞)\nArrows: Hasse arrows, with X >= Y iff Ext^{>0}(X, Y) = 0"
            }
            url="/api/quiver/gen-tilt"
            physicOption={HassePhysicsOption}
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <QuiverAccordion
            header="Wakamatsu tilting quiver"
            description={
              "Vertices: Wakamatsu tilting modules\nArrows: Hasse arrows, with X >= Y iff Ext^{>0}(X, Y) = 0\nRaise error if not poset."
            }
            url="/api/quiver/w-tilt"
            physicOption={HassePhysicsOption}
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <SubcatHasseAccordion
            events={{
              selectNode: handleSelectNode,
            }}
          />
          <PairHasseAccordion
            events={{
              selectNode: handleSelectNode,
            }}
          />
        </>
      )}
    </>
  );
}
