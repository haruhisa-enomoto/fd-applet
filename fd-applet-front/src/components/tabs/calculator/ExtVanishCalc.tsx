import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Autocomplete,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";

import CalcProps from "./CalcProps";

export default function ExtVanishCalc({ options }: CalcProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [mYY, setmYY] = useState<string[]>([]);
  const [valueY, setValueY] = useState("");
  const [result, setResult] = useState<boolean>();

  const [computeClicked, setComputeClicked] = useState<boolean>(false);

  async function getHom() {
    const actualXX = mXX;
    const actualYY = mYY;
    if (valueX !== "") {
      setmXX(mXX.concat(valueX));
      actualXX.push(valueX);
      setValueX("");
      console.log(actualXX);
    }
    if (valueY !== "") {
      setmYY(mYY.concat(valueY));
      actualYY.push(valueY);
      setValueY("");
      console.log(actualYY);
    }

    const response = await fetchWithUiFeedback<boolean>({
      url: "/api/calculator/2/ext-zero",
      method: "POST",
      body: { first: actualXX, second: actualYY },
    });

    if (response.data === undefined) return;
    setResult(response.data);
    setComputeClicked(true);
    setSelected(actualXX);
    setSecondarySelected(actualYY);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Vanishing of higher Ext</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Check whether Ext^i(X, Y) = 0 for all {"i > 0"} for modules X and Y.
        </Typography>
        <Grid
          container
          spacing={1}
          mb={2}
        >
          <Grid item xs={5}>
            <Autocomplete
              freeSolo
              multiple
              value={mXX}
              onChange={(_, newValue: string[]) => {
                setmXX(newValue);
                setSelected(newValue);
                setSecondarySelected(mYY);
                setHighlighted([]);
                setComputeClicked(false);
              }}
              inputValue={valueX}
              onInputChange={(event, newInputValue) => {
                setValueX(newInputValue);
                setComputeClicked(false);
              }}
              options={options}
              sx={{ width: "auto" }}
              renderInput={(params) => <TextField {...params} label="X" />}
            />
          </Grid>
          <Grid item xs={5}>
            <Autocomplete
              freeSolo
              multiple
              value={mYY}
              onChange={(_, newValue: string[]) => {
                setmYY(newValue);
                setSelected(mXX);
                setSecondarySelected(newValue);
                setHighlighted([]);
                setComputeClicked(false);
              }}
              inputValue={valueY}
              onInputChange={(event, newInputValue) => {
                setValueY(newInputValue);
                setComputeClicked(false);
              }}
              options={options}
              sx={{ width: "auto" }}
              renderInput={(params) => <TextField {...params} label="Y" />}
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getHom} />
          </Grid>
        </Grid>
        <Typography align="center">
          Ext^{"{>0}"}({mXX.join(" + ")}, {mYY.join(" + ")}) = 0 is
          {computeClicked && result !== undefined && ` ${result}`}
        </Typography>
      </AccordionDetails>
    </Accordion>
  );
}
