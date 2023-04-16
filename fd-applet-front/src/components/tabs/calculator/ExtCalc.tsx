import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Autocomplete,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import { useUi } from "../../../contexts/UiContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";

import CalcProps from "./CalcProps";

export default function ExtCalc({ options }: CalcProps) {
  const { setOpenNotify, setNotifyStatus } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [mYY, setmYY] = useState<string[]>([]);
  const [valueY, setValueY] = useState("");
  const [degree, setDegree] = useState<string>("0");
  const [result, setResult] = useState<number>();

  const [computeClicked, setComputeClicked] = useState<boolean>(false);

  async function getHom() {
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
    const n = parseInt(degree);
    if (isNaN(n) || n < 0) {
      setNotifyStatus({
        message: "n must be a non-negative integer.",
        severity: "error",
      });
      setOpenNotify(true);
      return;
    }

    const response = await fetchWithUiFeedback<number>({
      url: "/api/calculator/2/ext",
      method: "POST",
      body: { first: actualXX, second: actualYY, third: n },
    });
    if (response.data === undefined) return;
    setComputeClicked(true);
    setResult(response.data);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Hom and Ext</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Typography variant="subtitle1">
          Compute dim Ext^n(X, Y) for modules X and Y (Hom if n = 0).
        </Typography>
        <Grid
          container
          spacing={1}
          mt={1}
          alignItems="center"
          justifyContent="center"
          display="flex"
          sx={{ width: "auto" }}
        >
          <Grid item xs={4}>
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
          <Grid item xs={4}>
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
          <Grid item xs={2}>
            <TextField
              value={degree}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setDegree(event.target.value);
                setComputeClicked(false);
              }}
              label="n"
            />
          </Grid>
          <Grid item xs={2}>
            <ComputeButton onClick={getHom} />
          </Grid>
          <Grid item xs={12}>
            <Typography m={2}>
              dim Ext^{degree}({mXX.join(" + ")}, {mYY.join(" + ")}) ={" "}
              {computeClicked && result}
            </Typography>
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
