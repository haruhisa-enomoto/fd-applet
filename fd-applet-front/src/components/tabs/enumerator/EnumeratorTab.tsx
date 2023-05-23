import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import UpdateButton from "../../common/UpdateButton";
import MyTabProps from "../MyTabProps";

import IndecEnumerator from "./IndecEnumerator";
import ModuleEnumerator from "./ModuleEnumerator";
import ModuleWithDegreeEnumerator from "./ModuleWithDegreeEnumerator";
import PairEnumerator from "./PairEnumerator";
import SubcatEnumerator from "./SubcatEnumerator";

export default function EnumerateTab({
  isComputed,
  setIsComputed,
}: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  async function makeRfAlgebra() {
    const response = await fetchWithUiFeedback({
      url: "/api/rf-algebra",
      expectJson: false,
      showSuccess: false,
    });
    if (!response.success) return;
    setIsComputed(true);
  }

  return (
    <>
      {!isComputed ? (
        <UpdateButton onClick={makeRfAlgebra} />
      ) : (
        <>
          <ModuleEnumerator />
          <ModuleWithDegreeEnumerator />
          <SubcatEnumerator />
          <IndecEnumerator />
          <PairEnumerator />
        </>
      )}
    </>
  );
}
