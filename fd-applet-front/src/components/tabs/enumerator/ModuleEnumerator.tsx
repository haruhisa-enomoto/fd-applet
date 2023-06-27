import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import ListWithIndecList from "../../common/ListWithIndecList";

import StatisticsDialog from "./StatisticsDialog";

const moduleOptions: ComboOption[] = [
  {
    key: "rigid",
    label: "Rigid modules",
    description: "Modules M with Ext^1(M, M) = 0",
  },
  {
    key: "self_ortho",
    label: "Self-orthogonal modules",
    description: "Modules M with Ext^i(M, M) = 0 for all i > 0",
  },
  {
    key: "excep",
    label: "Exceptional modules",
    description: "Self-orthogonal modules with pd < ∞",
  },
  { key: "-", label: "-" },
  {
    key: "partial_tilt",
    label: "Classical partial tilting modules (pd <= 1)",
    description: "Rigid modules with pd <= 1",
  },
  {
    key: "tilt", label: "Classical tilting modules (pd <= 1)",
    description: "= maximal partial tilting modules"
  },
  { key: "-", label: "-" },
  {
    key: "partial_cotilt",
    label: "Classical partial cotilting modules (id <= 1)",
    description: "Rigid modules with id <= 1",
  },
  {
    key: "cotilt", label: "Classical cotilting modules (id <= 1)",
    description: "= maximal partial cotilting modules"
  },
  { key: "-", label: "-" },
  {
    key: "gen_tilt",
    label: "Generalized tilting modules (pd < ∞)",
    description: "Miyashita tilting modules = modules which are tilting complexes",
  },
  {
    key: "gen_cotilt",
    label: "Generalized cotilting modules (id < ∞)",
    description: "Dual of generalized tilting modules",
  },
  {
    key: "w_tilt",
    label: "Wakamatsu tilting modules",
    description: "= maximal self-orthogonal modules",
  },
  {
    key: "pure_w_tilt",
    label: "Wakamatsu tilting but non-(co)tilting modules",
    description: "Wakamatsu tilting, but neither tilting nor cotilting modules",
  },
  { key: "-", label: "-" },
  {
    key: "sbrick",
    label: "Semibricks",
    description: "Pair-wise Hom-orthogonal bricks",
  },
  { key: "-", label: "-" },
  { key: "s_tau_tilt", label: "Support τ-tilting modules" },
  { key: "tau_tilt", label: "τ-tilting modules" },
  { key: "tau_rigid", label: "τ-rigid modules" },
  { key: "-", label: "-" },
  { key: "s_tau_minus_tilt", label: "Support τ^{-}-tilting modules" },
  { key: "tau_minus_tilt", label: "τ^{-}-tilting modules" },
  { key: "tau_minus_rigid", label: "τ^{-}-rigid modules" },
];

export default function ModuleEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("tilt");
  const [data, setData] = useState<string[][]>([]);

  async function getData() {
    const response = await fetchWithUiFeedback<string[][]>({
      url: "/api/module/" + selectedMenu,
      showDuration: true
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Modules (tiltings, semibricks, ...)</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          mb={2}
        >
          <Grid item xs={10}>
            <Combo
              title="Choose types"
              options={moduleOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <ListWithIndecList
          candidates={data}
          leftHeader="Modules"
          handleChange={(value) => {
            setSelected(value);
            setSecondarySelected([]);
            setHighlighted([]);
          }}
        />
        {data && <StatisticsDialog data={data} />}
      </AccordionDetails>
    </Accordion>
  );
}
