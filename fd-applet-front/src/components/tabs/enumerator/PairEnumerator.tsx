import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Grid,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import LargePairLists from "../../common/LargePairLists";
import { Pair } from "../../common/PairTable";

export const pairOptions: ComboOption[] = [
  {
    key: "tors",
    label: "Torsion pairs",
    description: "1st: torsion class T, \n2nd: torsion-free class F",
  },
  {
    key: "tau-tilt",
    label: "τ-tilting pairs",
    description: "1st: support τ-tilting modules, \n2nd: support part (projs)",
  },
  { key: "-", label: "-" },
  {
    key: "cotors",
    label: "Cotorsion pairs",
    description: "1st: X, 2nd: Y, \nwith Ext^1(X, Y) = 0",
  },
  {
    key: "h-cotors",
    label: "Hereditary cotorsion pairs",
    description:
      "1st: X (resolving), 2nd: Y (coresolving), \nwith Ext^i(X, Y) = 0 for i > 0",
  },

  { key: "-", label: "-" },
  {
    key: "2-smc",
    label: "2-simple minded collections",
    description: "1st: (mod A) part, \n2nd: (mod A) [1] part",
  },
  {
    key: "sbrick-full-rank",
    label: "Semibrick pairs of full rank",
    description: "1st: (mod A) part, \n2nd: (mod A) [1] part",
  },
  {
    key: "sbrick-maximal",
    label: "Maximal semibrick pairs",
    description: "1st: (mod A) part, \n2nd: (mod A) [1] part",
  },
];

export default function SubcatEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("tors");
  const [data, setData] = useState<Pair<string[], string[]>[]>([]);

  async function getData() {
    const response = await fetchWithUiFeedback<Pair<string[], string[]>[]>({
      url: "/api/subcat/pair/" + selectedMenu,
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Pair of objects ((co)torsion pairs, 2-SMC, ...)</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Typography m={1}>
          Blue: 1st part, Red: 2nd part, Purple: both parts
        </Typography>
        <Grid
          container
          spacing={2}
          justifyContent="space-around"
          alignItems="center"
        >
          <Grid item xs={10}>
            <Combo
              title="Choose types"
              options={pairOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
              showTooltips={false}
              showDescriptions={true}
            />
          </Grid>
          <Grid item xs={2}>
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>

        <LargePairLists
          data={data}
          header1="List of pairs"
          header2="1st (blue)"
          header3="2nd (red)"
          setSelected={setSelected}
          setSecondarySelected={setSecondarySelected}
          setHighlighted={setHighlighted}
        />
      </AccordionDetails>
    </Accordion>
  );
}
