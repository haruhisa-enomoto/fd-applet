import { useState } from "react";

import {
  FormControl,
  FormControlLabel,
  FormLabel,
  Grid,
  Radio,
  RadioGroup,
  Typography,
} from "@mui/material";

import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import UpdateButton from "../../common/UpdateButton";
import MyTabProps from "../MyTabProps";

const subcatOptions: ComboOption[] = [
  { key: "tors", label: "Torsion class" },
  { key: "torf", label: "Torsion-free class" },
  { key: "-", label: "-" },
  // { key: "serre", label: "Serre subcategory" },
  { key: "wide", label: "Wide subcategory" },
  { key: "-", label: "-" },
  { key: "ice", label: "ICE-closed subcategory" },
  { key: "ike", label: "IKE-closed subcategory" },
  { key: "-", label: "-" },
  { key: "ie", label: "IE-closed subcategory" },
  { key: "-", label: "-" },
  { key: "subcat", label: "Subcategory" },
];

const moduleOptions: ComboOption[] = [
  { key: "ext-proj", label: "Ext-projective objects" },
  { key: "ext-inj", label: "Ext-injective objects" },
  { key: "-", label: "-" },
  { key: "s-tau-tilt", label: "Support τ-tilting module" },
  { key: "s-tau-minus-tilt", label: "Support τ^{-}-tilting module" },
  { key: "-", label: "-" },
  { key: "sbrick", label: "Semibrick" },
  { key: "-", label: "-" },
  { key: "module", label: "Module" },
];

const operations: Record<string, Record<string, string>> = {
  tors: {
    torf: "T to T^{\\perp}",
    wide: "T to α(T) (Marks-Stovicek)",
    "s-tau-tilt": "T to its Ext-projective objects",
    "ext-proj": "T to its Ext-projective objects",
    "ext-inj": "T to its Ext-injective objects",
    sbrick: "T to S such that T is the torsion closure of S (Asai)",
  },
  torf: {
    tors: "F to ^{\\perp}F",
    wide: "F to β(F) (Marks-Stovicek)",
    "s-tau-minus-tilt": "F to its Ext-injective objects",
    "ext-proj": "F to its Ext-projective objects",
    "ext-inj": "F to its Ext-injective objects",
    sbrick: "F to S such that F is the torsion-free closure of S (Asai)",
  },
  wide: {
    tors: "W to its torsion closure",
    torf: "W to its torsion-free closure",
    "ext-proj": "W to its Ext-projective objects",
    "ext-inj": "W to its Ext-injective objects",
    sbrick: "W to its simple objects",
  },
  ice: {
    wide: "C to α(C)",
    "ext-proj": "C to its Ext-projective objects",
    "ext-inj": "C to its Ext-injective objects",
  },
  ike: {
    wide: "C to β(C)",
    "ext-proj": "C to its Ext-projective objects",
    "ext-inj": "C to its Ext-injective objects",
  },
  ie: {
    "ext-proj": "C to its Ext-projective objects",
    "ext-inj": "C to its Ext-injective objects",
  },
  subcat: {
    "ext-proj": "C to its Ext-projective objects",
    "ext-inj": "C to its Ext-injective objects",
  },
  "ext-proj": {},
  "ext-inj": {},
  "s-tau-tilt": {
    tors: "M to Fac M (Adachi-Iyama-Reiten)",
    sbrick: "M to S such that Fac M is the torsion closure of S (Asai)",
  },
  "s-tau-minus-tilt": {},
  sbrick: {},
  module: {},
};

export default function ConverterTab({
  isComputed,
  setIsComputed,
}: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [indecs, setIndecs] = useState<string[]>([]);

  const [fromType, setFromType] = useState<string>("subcat");
  const [toType, setToType] = useState<string>("subcat");

  const [fromClass, setFromClass] = useState<string>("subcat");
  const [toClass, setToClass] = useState<string>("subcat");

  async function getIndecs() {
    const response = await fetchWithUiFeedback<string[]>({
      url: "/api/algebra-info/strings",
      showSuccess: false,
    });
    if (response.data === undefined) return;
    setIndecs(response.data);
    setIsComputed(true);
  }

  return (
    <>
      {!isComputed && <UpdateButton onClick={getIndecs} />}
      {isComputed && (
        <>
          <Typography m={2}>
            You can convert between several types of modules or subcategories.
            For example:
            <li>From modules to the smallest torsion class containing them.</li>
            <li>
              From (extension-closed) subcategories to its Ext-projective
              objects.
            </li>
            <li>From wide subcategories to the corresponding torsion class.</li>
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <FormControl sx={{ m: 1 }}>
                <FormLabel>Convert from</FormLabel>
                <RadioGroup
                  defaultValue="module"
                  onChange={(e) => setFromType(e.target.value)}
                  value={fromType}
                  row
                >
                  <FormControlLabel
                    value="module"
                    control={<Radio />}
                    label="Module"
                  />
                  <FormControlLabel
                    value="subcat"
                    control={<Radio />}
                    label="Subcategory"
                  />
                </RadioGroup>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <FormControl sx={{ m: 1 }}>
                <FormLabel>To</FormLabel>
                <RadioGroup
                  defaultValue="module"
                  onChange={(e) => setToType(e.target.value)}
                  value={toType}
                  row
                >
                  <FormControlLabel
                    value="module"
                    control={<Radio />}
                    label="Module"
                  />
                  <FormControlLabel
                    value="subcat"
                    control={<Radio />}
                    label="Subcategory"
                  />
                </RadioGroup>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <Combo
                title="From"
                options={fromType === "subcat" ? subcatOptions : moduleOptions}
                selected={fromClass}
                setSelected={setFromClass}
              />
            </Grid>
            <Grid item xs={6}>
              <Combo
                title="To"
                options={toType === "subcat" ? subcatOptions : moduleOptions}
                selected={toClass}
                setSelected={setToClass}
              />
            </Grid>
          </Grid>
          <Typography m={2}>{operations[fromClass][toClass] ?? ""}</Typography>
        </>
      )}
    </>
  );
}
