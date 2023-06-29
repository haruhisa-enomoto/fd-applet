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

const options: ComboOption[] = [
  {
    key: "mgs",
    label: "Maximal green sequences",
    description: "Maximal paths in the support τ-tilting quiver represented by brick labels",
  },
];

export default function OtherEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("mgs");
  const [data, setData] = useState<string[][]>([]);

  async function getData() {
    const response = await fetchWithUiFeedback<string[][]>({
      url: "/api/others/" + selectedMenu,
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
        <Typography fontWeight="medium">Others (maximal green sequences, ...)</Typography>
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
              options={options}
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
          leftHeader="Results"
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
