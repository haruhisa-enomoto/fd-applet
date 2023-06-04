import { useMemo, useState } from "react";

import { Grid } from "@mui/material";

import LargeList from "./LargeList";

interface ListWithIndecProps {
  candidates: string[][];
  handleChange: (value: string[]) => void;
  handleIndexChange?: (index: number) => void;
  leftHeader: string;
  rightHeader?: string;
}

export default function ListWithIndecList({ candidates, handleChange,
  handleIndexChange = () => {
    // do nothing
  },
  leftHeader, rightHeader = "Indecs in it"
}: ListWithIndecProps) {
  const [index, setIndex] = useState<number>(0);
  const candidatesStrs = useMemo(() => candidates.map((candidate) => candidate.join(", ")), [candidates]);

  return (
    <Grid
      container
      spacing={2}
      mb={2}
    >
      <Grid item xs={6}>
        <LargeList
          header={leftHeader}
          data={candidatesStrs}
          onSelect={(index) => {
            setIndex(index);
            handleIndexChange(index);
            handleChange(candidates[index]);
          }}
        />
      </Grid>
      <Grid item xs={6}>
        {candidates[index] && (
          <LargeList header={rightHeader} data={candidates[index]} text />
        )}
      </Grid>
    </Grid >
  );
}