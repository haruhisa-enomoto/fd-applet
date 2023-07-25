import { useMemo, useState } from "react";

import { Grid } from "@mui/material";

import LargeList from "./LargeList";

interface ListListListProps {
  candidates: string[][][];
  handleMiddleChange: (value: string[]) => void;
  leftHeader?: string;
  middleHeader?: string;
  rightHeader?: string;
}

export default function ListListList({
  candidates,
  handleMiddleChange,
  leftHeader = "Sequences",
  middleHeader = "Subcats",
  rightHeader = "Indecs in it"
}: ListListListProps) {
  const [leftIndex, setLeftIndex] = useState<number>(0);
  const [middleIndex, setMiddleIndex] = useState<number>(0);
  const candidatesStrs = useMemo(() => candidates.map((candidate) => candidate.join(" ⊇ ")), [candidates]);
  const middleStrs = useMemo(() => {
    if (candidates[leftIndex] === undefined) return [];
    return candidates[leftIndex].map((candidate) => candidate.join(", "));
  }, [candidates, leftIndex]
  );

  return (
    <Grid
      container
      spacing={2}
      mb={2}
    >
      <Grid item xs={4}>
        <LargeList
          header={leftHeader}
          data={candidatesStrs}
          onSelect={(index) => {
            setLeftIndex(index);
          }}
        />
      </Grid>
      <Grid item xs={4}>
        {candidates[leftIndex] && (
          <LargeList
            header={middleHeader}
            data={middleStrs}
            onSelect={(index) => {
              setMiddleIndex(index);
              handleMiddleChange(candidates[leftIndex][index]);
            }}
          />
        )}
      </Grid>
      <Grid item xs={4}>
        {candidates[leftIndex] && candidates[leftIndex][middleIndex] && (
          <LargeList header={rightHeader} data={candidates[leftIndex][middleIndex]} text />
        )}
      </Grid >
    </Grid>
  );
}