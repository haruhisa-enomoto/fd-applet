import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Autocomplete,
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  Radio,
  RadioGroup,
  TextField,
  Typography,
} from "@mui/material";

import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";

import CalcProps from "./CalcProps";

export default function StableHomCalc({ options }: CalcProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [mYY, setmYY] = useState<string[]>([]);
  const [valueY, setValueY] = useState("");
  const [result, setResult] = useState<number>();

  const [homType, setHomType] = useState<string>("proj");

  const [computeClicked, setComputeClicked] = useState<boolean>(false);

  async function getStableHom() {
    const actualXX = mXX;
    const actualYY = mYY;
    if (valueX !== "") {
      setmXX(mXX.concat(valueX));
      actualXX.push(valueX);
      setValueX("");
    }
    if (valueY !== "") {
      setmYY(mYY.concat(valueY));
      actualYY.push(valueY);
      setValueY("");
    }

    const response = await fetchWithUiFeedback<number>({
      url: `/api/calculator/2/${homType}-st-hom`,
      method: "POST",
      body: { first: actualXX, second: actualYY },
    });
    if (response.data === undefined) return;
    setComputeClicked(true);
    setResult(response.data);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Stable Hom</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Compute proj/inj-stable Hom (X, Y) for modules X and Y.
        </Typography>
        <FormControl sx={{ m: 1 }}>
          <FormLabel>Type</FormLabel>
          <RadioGroup
            defaultValue="proj"
            onChange={(_, value) => {
              setHomType(value);
              setComputeClicked(false);
            }}
            value={homType}
            row
          >
            <FormControlLabel
              value="proj"
              control={<Radio />}
              label="Projectively stable"
            />
            <FormControlLabel
              value="inj"
              control={<Radio />}
              label="Injectively stable"
            />
          </RadioGroup>
        </FormControl>
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
            <ComputeButton onClick={getStableHom} />
          </Grid>
        </Grid>
        <Typography align="center">
          dim {homType}-stable Hom ({mXX.join(" + ")}, {mYY.join(" + ")}) ={" "}
          {computeClicked && result}
        </Typography>
      </AccordionDetails>
    </Accordion>
  );
}
