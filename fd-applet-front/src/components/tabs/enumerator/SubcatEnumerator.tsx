import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  Radio,
  RadioGroup,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import LargeList from "../../common/LargeList";

import StatisticsDialog from "./StatisticsDialog";

export const subcatOptions: ComboOption[] = [
  {
    key: "tors",
    label: "Torsion classes",
    description: "Subcats closed under quotients and extensions",
  },
  {
    key: "torf",
    label: "Torsion-free classes",
    description: "Subcats closed under submodules and extensions",
  },
  {
    key: "wide",
    label: "Wide subcategories",
    description: "Subcats closed under kernels, cokernels, and extensions",
  },
  { key: "-", label: "-" },
  {
    key: "ie",
    label: "IE-closed subcategories",
    description: "Subcats closed under images and extensions",
  },
  {
    key: "ice",
    label: "ICE-closed subcategories",
    description: "Subcats closed under images, cokernels, and extensions",
  },
  {
    key: "ike",
    label: "IKE-closed subcategories",
    description: "Subcats closed under images, kernels, and extensions",
  },
  { key: "-", label: "-" },
  {
    key: "resolving",
    label: "Resolving subcategories",
    description:
      "Subcats closed under epi-kernels and extensions containing all projs",
  },
  {
    key: "coresolving",
    label: "Coresolving subcategories",
    description:
      "Subcats closed under mono-cokernels and extensions containing all injs",
  },
];

export default function SubcatEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("tors");
  const [data, setData] = useState<string[][]>([]);
  const [index, setIndex] = useState<number>(0);

  const [selectedValue, setSelectedValue] = useState("none");

  async function getData() {
    const response = await fetchWithUiFeedback<string[][]>({
      url: "/api/subcat/" + selectedMenu,
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  async function highlightModules(event: React.ChangeEvent<HTMLInputElement>) {
    const type = event.target.value;
    setSelectedValue(type);
    if (type === "none") {
      setHighlighted([]);
      return;
    }
    const response = await fetchWithUiFeedback<string[]>({
      url: "/api/calculator/subcat/" + type,
      method: "POST",
      body: data[index],
      showSuccess: false,
    });
    if (response.data === undefined) return;
    setHighlighted(response.data);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Subcategories (torsion classes, resolving, ...)</Typography>
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
              options={subcatOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={2}>
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <Grid container spacing={1}>
          <Grid item xs={6}>
            <LargeList
              header="Subcats"
              data={data.map((modules) => modules.join(", "))}
              onSelect={(index) => {
                setSelected(data[index]);
                setIndex(index);
                setHighlighted([]);
                setSecondarySelected([]);
                setSelectedValue("none");
              }}
            />
          </Grid>
          <Grid item xs={6}>
            {data[index] && (
              <LargeList header="Indecs in it" data={data[index]} text />
            )}
          </Grid>
          <Grid item xs={12}>
            <FormControl sx={{ m: 1 }}>
              <FormLabel>Highlight</FormLabel>
              <RadioGroup
                defaultValue="none"
                name="highlight-modules"
                onChange={highlightModules}
                value={selectedValue}
                row
              >
                <FormControlLabel
                  value="none"
                  control={<Radio />}
                  label="None"
                />
                <FormControlLabel
                  value="proj"
                  control={<Radio />}
                  label="Ext-projectives"
                />
                <FormControlLabel
                  value="inj"
                  control={<Radio />}
                  label="Ext-injectives"
                />
              </RadioGroup>
            </FormControl>
          </Grid>
          <Grid item xs={12}>
            {data && <StatisticsDialog data={data} />}
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
