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
import LargeList from "../../common/LargeList";

import StatisticsDialog from "./StatisticsDialog";

const moduleOptions: ComboOption[] = [
  {
    key: "rigid",
    label: "Rigid modules",
    description: "Modules M with Ext^1(M, M) = 0",
  },
  {
    key: "self-ortho",
    label: "Self-orthogonal modules",
    description: "Modules M with Ext^i(M, M) = 0 for all i > 0",
  },
  {
    key: "excep",
    label: "Exceptional modules",
    description: "Self-orthogonal modules with finite proj. dim.",
  },
  { key: "-", label: "-" },
  {
    key: "partial-tilt",
    label: "Classical partial tilting modules (pd <= 1)",
    description: "Rigid modules with pd <= 1",
  },
  { key: "tilt", label: "Classical tilting modules (pd <= 1)" },
  { key: "-", label: "-" },
  {
    key: "partial-cotilt",
    label: "Classical partial cotilting modules (id <= 1)",
    description: "Rigid modules with id <= 1",
  },
  { key: "cotilt", label: "Classical cotilting modules (id <= 1)" },
  { key: "-", label: "-" },
  {
    key: "gen-tilt",
    label: "Generalized tilting modules (pd < ∞)",
    description: "Miyashita tilting modules",
  },
  {
    key: "gen-cotilt",
    label: "Generalized cotilting modules (id < ∞)",
    description: "Miyashita cotilting modules",
  },
  {
    key: "w-tilt",
    label: "Wakamatsu tilting modules",
    description: "= maximal self-orthogonal modules",
  },
  {
    key: "pure-w-tilt",
    label: "Wakamatsu tilting but non-(co)tilting modules",
    description: "Wakamatsu tilting, but neither tilting nor cotilting",
  },
  { key: "-", label: "-" },
  {
    key: "sbrick",
    label: "Semibricks",
    description: "Pair-wise Hom-orthogonal bricks",
  },
  { key: "-", label: "-" },
  { key: "s-tau-tilt", label: "Support τ-tilting modules" },
  { key: "tau-tilt", label: "τ-tilting modules" },
  { key: "tau-rigid", label: "τ-rigid modules" },
  { key: "-", label: "-" },
  { key: "s-tau-minus-tilt", label: "Support τ^{-}-tilting modules" },
  { key: "tau-minus-tilt", label: "τ^{-}-tilting modules" },
  { key: "tau-minus-rigid", label: "τ^{-}-rigid modules" },
];

export default function ModuleEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("tilt");
  const [data, setData] = useState<string[][]>([]);
  const [index, setIndex] = useState<number>(0);

  async function getData() {
    const response = await fetchWithUiFeedback<string[][]>({
      url: "/api/module/" + selectedMenu,
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
        <Typography>Modules (tiltings, semibricks, ...)</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          justifyContent="space-around"
          alignItems="center"
        >
          <Grid item xs={10}>
            <Combo
              title="Choose types"
              options={moduleOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={2}>
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <LargeList
              header="Modules"
              data={data.map((modules) => modules.join(", "))}
              onSelect={(index) => {
                setSelected(data[index]);
                setHighlighted([]);
                setSecondarySelected([]);
                setIndex(index);
              }}
            />
          </Grid>
          <Grid item xs={6}>
            {data[index] && (
              <LargeList header="Indecs in it" data={data[index]} text />
            )}
          </Grid>
          <Grid item xs={12}>
            {data && <StatisticsDialog data={data} />}
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
