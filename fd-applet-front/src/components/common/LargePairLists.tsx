import { useMemo, useState } from "react";

import { Grid } from "@mui/material";

import { useSelection } from "../../contexts/SelectionContext";

import LargeList from "./LargeList";
import { Pair } from "./PairTable";

interface LargePairListsProps {
  data: Pair<string[], string[]>[];
  header1: string;
  header2: string;
  header3: string;
}

function joinPair(pair: Pair<string[], string[]>): string {
  return `${pair.first.join(", ")} | ${pair.second.join(", ")}`;
}

export default function LargePairLists({
  data,
  header1,
  header2,
  header3,
}: LargePairListsProps) {
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [index, setIndex] = useState<number>(0);
  const dataMemo = useMemo(() => data.map((pair) => joinPair(pair)), [data]);

  const handleSelect = (index: number) => {
    setSelected(data[index].first);
    setSecondarySelected(data[index].second);
    setIndex(index);
    setHighlighted([]);
  };
  return (
    <Grid container spacing={1} mb={2}>
      <Grid item xs={4}>
        <LargeList
          header={header1}
          data={dataMemo}
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
