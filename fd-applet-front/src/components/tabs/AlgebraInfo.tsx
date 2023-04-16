import { useState } from "react";

import { Typography } from "@mui/material";

import useFetchWithUiFeedback from "../../hooks/useFetchWithUiFeedback";
import { Pair, PairTable } from "../common/PairTable";
import UpdateButton from "../common/UpdateButton";

import MyTabProps from "./MyTabProps";

type AlgebraInfo = {
  className: string;
  dimInfo: Pair<string, number | null>[];
  indecInfo: Pair<string, number | null>[];
  namedModules: Pair<string, string>[];
};

export default function AlgebraInfoTab({
  isComputed,
  setIsComputed,
}: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const [info, setInfo] = useState<AlgebraInfo>();

  async function getInfo() {
    const response = await fetchWithUiFeedback<AlgebraInfo>({
      url: "/api/algebra-info",
    });
    if (response.data === undefined) return;
    setIsComputed(true);
    setInfo(response.data);
  }

  return (
    <>
      {!isComputed && <UpdateButton onClick={getInfo} />}
      {isComputed && info && (
        <>
          <Typography variant="h6" m={2}>
            {info.className}
          </Typography>
          <PairTable
            firstName="Various Dimensions"
            secondName="Values"
            data={info.dimInfo}
          />
          <PairTable
            firstName="Number of indecomposable modules"
            secondName="Number"
            data={info.indecInfo}
          />
          <PairTable
            firstName="Named module"
            secondName="Word"
            data={info.namedModules}
          />
        </>
      )}
    </>
  );
}
