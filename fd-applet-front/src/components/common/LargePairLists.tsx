import { useState } from "react";

import { Grid } from "@mui/material";

import LargeList from "./LargeList";
import { Pair } from "./PairTable";

interface LargePairListsProps {
  data: Pair<string[], string[]>[];
  header1: string;
  header2: string;
  header3: string;
  setSelected: (value: string[]) => void;
  setSecondarySelected: (value: string[]) => void;
  setHighlighted: (value: string[]) => void;
}

function joinPair(pair: Pair<string[], string[]>): string {
  return `${pair.first.join(", ")} | ${pair.second.join(", ")}`;
}

export default function LargePairLists({
  data,
  header1,
  header2,
  header3,
  setSelected,
  setSecondarySelected,
  setHighlighted,
}: LargePairListsProps) {
  const [index, setIndex] = useState<number>(0);

  const handleSelect = (index: number) => {
    setSelected(data[index].first);
    setSecondarySelected(data[index].second);
    setIndex(index);
    setHighlighted([]);
  };
  return (
    <Grid container spacing={2} minWidth={0}>
      <Grid item xs={4}>
        <LargeList
          header={header1}
          data={data.map((pair) => joinPair(pair))}
          onSelect={handleSelect}
        />
      </Grid>
      <Grid item xs={4}>
        {data[index] && (
          <LargeList header={header2} data={data[index].first} text />
        )}
      </Grid>
      <Grid item xs={4}>
        {data[index] && (
          <LargeList header={header3} data={data[index].second} text />
        )}
      </Grid>
    </Grid>
  );
}
