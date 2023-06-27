import { useState } from "react";


import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import UpdateButton from "../../common/UpdateButton";
import MyTabProps from "../MyTabProps";

import ExtCalc from "./ExtCalc";
import ExtVanishCalc from "./ExtVanishCalc";
import HomologicalDimCalc from "./HomologicalDimCalc";
import InjResolCalc from "./InjResolCalc";
import ProjResolCalc from "./ProjResolCalc";
import StableHomCalc from "./StableHomCalc";

export default function CalculatorTab({
  isComputed,
  setIsComputed,
}: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [options, setOptions] = useState<string[]>([]);

  async function getOptions() {
    const response = await fetchWithUiFeedback<string[]>({
      url: "/api/calculator/candidates",
      showSuccess: false,
    });
    if (response.data === undefined) return;
    setOptions(response.data);
    setIsComputed(true);
  }

  return (
    <>
      {!isComputed && <UpdateButton onClick={getOptions} />}
      {isComputed && (
        <>
          <ExtCalc options={options} />
          <StableHomCalc options={options} />
          <ExtVanishCalc options={options} />
          <ProjResolCalc options={options} />
          <InjResolCalc options={options} />
          <HomologicalDimCalc options={options} />
        </>
      )}
    </>
  );
}
