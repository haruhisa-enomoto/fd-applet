import { useState, useEffect } from "react";

import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import {
  Box,
  Button,
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
import LargeList from "../../common/LargeList";
import ListWithIndecList from "../../common/ListWithIndecList";
import UpdateButton from "../../common/UpdateButton";
import MyTabProps from "../MyTabProps";


type OperationType = "module" | "subcat";

const displayNames: Record<string, { label: string; type: OperationType; }> = {
  tors: { label: "Torsion class", type: "subcat" },
  tors_closure: { label: "Torsion class (closure)", type: "subcat" },
  tors_as_perp: { label: "Torsion class (Hom-perp)", type: "subcat" },
  torf: { label: "Torsion-free class", type: "subcat" },
  torf_closure: { label: "Torsion-free class (closure)", type: "subcat" },
  torf_as_perp: { label: "Torsion-free class (Hom-perp)", type: "subcat" },
  wide: { label: "Wide subcategory", type: "subcat" },
  wide_closure: { label: "Wide subcategory (closure)", type: "subcat" },
  ice: { label: "ICE-closed subcategory", type: "subcat" },
  ice_closure: { label: "ICE-closed subcategory (closure)", type: "subcat" },
  ike: { label: "IKE-closed subcategory", type: "subcat" },
  ike_closure: { label: "IKE-closed subcategory (closure)", type: "subcat" },
  ie: { label: "IE-closed subcategory", type: "subcat" },
  ie_closure: { label: "IE-closed subcategory (closure)", type: "subcat" },
  subcat: { label: "Subcategory (general)", type: "subcat" },
  s_tau_tilt: { label: "Support τ-tilting module", type: "module" },
  s_tau_minus_tilt: { label: "Support τ^{-}-tilting module", type: "module" },
  sbrick: { label: "Semibrick", type: "module" },
  ext_proj: { label: "Ext-projective objects", type: "module" },
  ext_inj: { label: "Ext-injective objects", type: "module" },
  module: { label: "Module (general)", type: "module" },
  "-": { label: "-", type: "subcat" },
  "-subcat-": { label: "-", type: "subcat" },
  "-module-": { label: "-", type: "module" },
};

function outputNameToInput(name: string) {
  if (name == "tors_closure") return "tors";
  if (name == "tors_as_perp") return "tors";
  if (name == "torf_closure") return "torf";
  if (name == "torf_as_perp") return "torf";
  if (name == "wide_closure") return "wide";
  if (name == "ice_closure") return "ice";
  if (name == "ike_closure") return "ike";
  if (name == "ie_closure") return "ie";
  if (name == "ext_proj") return "module";
  if (name == "ext_inj") return "module";
  return name;
}

const rawSubcatOptions: string[] = [
  "tors",
  "torf",
  "-",
  "wide",
  "-",
  "ice",
  "ike",
  "-",
  "ie",
  "-",
  "subcat",
];

const rawModuleOptions: string[] = [
  "s_tau_tilt",
  "s_tau_minus_tilt",
  "-",
  "sbrick",
  "-",
  "module",
];

const subcatOptions: ComboOption[] = rawSubcatOptions.map((key) => ({
  key,
  label: displayNames[key].label,
}));

const moduleOptions: ComboOption[] = rawModuleOptions.map((key) => ({
  key,
  label: displayNames[key].label,
}));

const operations: Record<string, Record<string, string>> = {
  // From subcat
  tors: {
    torf: "T to T^{\\perp}",
    wide: "T to α(T) = W_L (T)",
    "-subcat-": "",
    wide_closure: "T to its wide closure",
    sbrick: "T to S such that T is the torsion closure of S",
    s_tau_tilt: "T to its Ext-projective objects",
    "-module-": "",
    ext_proj: "T to its Ext-projective objects",
    ext_inj: "T to its Ext-injective objects",
  },
  torf: {
    tors: "F to F^{\\perp}",
    wide: "F to β(F) = W_R(F)",
    "-subcat-": "",
    wide_closure: "F to its wide closure",
    s_tau_minus_tilt: "F to its Ext-injective objects",
    sbrick: "F to S such that F is the torsion-free closure of S",
    "-module-": "",
    ext_proj: "F to its Ext-projective objects",
    ext_inj: "F to its Ext-injective objects",
  },
  wide: {
    tors: "W to its torsion closure",
    torf: "W to its torsion-free closure",
    sbrick: "W to its simple objects",
    "-module-": "",
    ext_proj: "W to its Ext-projective objects",
    ext_inj: "W to its Ext-injective objects",
  },
  ice: {
    wide: "C to α(C) = W_L(C) (contained in C)",
    "-subcat-": "",
    wide_closure: "C to its wide closure (C is tors in it)",
    tors_closure: "C to its torsion closure (C is Serre in it)",
    ext_proj: "C to its Ext-projective objects",
    ext_inj: "C to its Ext-injective objects",
  },
  ike: {
    wide: "C to β(C) = W_R(C) (contained in C)",
    "-subcat-": "",
    wide_closure: "C to its wide closure (C is torf in it)",
    torf_closure: "C to its torsion-free closure (C is Serre in it)",
    ext_proj: "C to its Ext-projective objects",
    ext_inj: "C to its Ext-injective objects",
  },
  ie: {
    tors_closure: "C to its torsion closure",
    torf_closure: "C to its torsion-free closure",
    wide_closure: "C to its wide closure",
    ice_closure: "C to its ICE-closure",
    ike_closure: "C to its IKE-closure",
    ext_proj: "C to its Ext-projective objects",
    ext_inj: "C to its Ext-injective objects",
  },
  subcat: {
    tors_closure: "C to its torsion closure",
    torf_closure: "C to its torsion-free closure",
    ie_closure: "C to its IE-closure",
    wide_closure: "C to its wide closure",
    ice_closure: "C to its ICE-closure",
    ike_closure: "C to its IKE-closure",
    ext_proj: "C to its Ext-projective objects",
    ext_inj: "C to its Ext-injective objects",
  },
  // From module
  s_tau_tilt: {
    tors: "M to Fac M",
    torf_as_perp: "M to M^\\perp",
    s_tau_minus_tilt: "M to N such that M^\\perp = Sub N",
    sbrick: "M to S such that Fac M is the torsion closure of S",
  },
  s_tau_minus_tilt: {
    torf: "M to Sub M",
    tors_as_perp: "M to ^\\perp M",
    s_tau_tilt: "M to N such that ^\\perp M = Fac N",
    sbrick: "M to S such that Sub M is the torsion-free closure of S",
  },
  sbrick: {
    wide: "S to Filt S",
    tors: "S to its torsion closure",
    torf: "S to its torsion-free closure",
    "-subcat-": "",
    tors_as_perp: "S to ^\\perp S",
    torf_as_perp: "S to S^\\perp",
    s_tau_tilt: "S to M s.t. Fac M is the torsion closure of S",
    s_tau_minus_tilt: "S to M s.t. Sub M is the torsion-free closure of S",
    sbrick: "S to T such that S ⊕ T[1] is 2-smc",
  },
  module: {
    tors_as_perp: "M to ^\\perp M",
    torf_as_perp: "M to M^\\perp",
    "-subcat-": "",
    tors_closure: "M to its torsion closure",
    torf_closure: "M to the torsion-free closure",
    wide_closure: "M to its wide closure",
    ice_closure: "M to its ICE-closure",
    ike_closure: "M to its IKE-closure",
    ie_closure: "M to its IE-closure",
  },
};


const getToOptions = (fromClass: string, toType: string): ComboOption[] => {
  const operationOptions = Object.keys(operations[fromClass]).map((key) => ({
    key,
    label: displayNames[key]?.label ?? key,
  }));

  // Only return options that match the current 'to' type
  return operationOptions.filter(
    (option) => displayNames[option.key].type === toType
  );
};

export default function ConverterTab({
  isComputed,
  setIsComputed,
}: MyTabProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [indecs, setIndecs] = useState<string[]>([]);
  const [candidates, setCandidates] = useState<string[][]>([]);

  const [from, setFrom] = useState<string[]>([]);

  const [fromType, setFromType] = useState<OperationType>("module");
  const [toType, setToType] = useState<OperationType>("module");

  const [fromClass, setFromClass] = useState<string>(rawModuleOptions[0]);
  const [toClass, setToClass] = useState<string>("");

  const [fromOptions, setFromOptions] = useState<ComboOption[]>(moduleOptions);
  const [toOptions, setToOptions] = useState<ComboOption[]>([]);

  const [candidateComputed, setCandidateComputed] = useState(false);
  const [inputMethod, setInputMethod] = useState("candidates");

  const [result, setResult] = useState<string[]>();

  const [forcedInput, setForcedInput] = useState<string[]>([]);

  async function getIndecs() {
    const response = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/all"
    });
    if (response.data === undefined) return;
    setIndecs(response.data);
    setIsComputed(true);
    return;
  }

  async function getCandidates(
    fromType: string,
    fromClass: string,
  ) {
    const response = await fetchWithUiFeedback<string[][]>({
      url: `/api/${fromType}/${fromClass}`
    });
    if (response.data === undefined) return;
    setCandidates(response.data);
    setCandidateComputed(true);
  }


  const handleFromChange = (newFrom: string[]) => {
    setFrom(newFrom);
    setSelected(newFrom);
    setSecondarySelected([]);
    setHighlighted([]);
  };

  async function fetchResult() {
    setSelected(from);
    setSecondarySelected([]);
    const response = await fetchWithUiFeedback<string[]>({
      url: `/api/converter/${fromClass}/${toClass}`,
      method: "POST",
      body: from,
    });
    if (response.data === undefined) return;
    setResult(response.data);
    setSecondarySelected(response.data);
  }

  const handleSendInput = () => {
    if (result === undefined) return;
    const currentFromClass = fromClass;
    const newFromType = toType;
    const newFromClass = outputNameToInput(toClass);
    const newToType = fromType;
    console.log(newFromType, newFromClass, newToType, currentFromClass);
    setFromType(newFromType);
    setFromClass(newFromClass);
    setToType(newToType);
    update(newFromType, newFromClass, newToType, currentFromClass);

    setInputMethod("manual");
    setForcedInput(result);
  };

  const update = (newFromType: OperationType, newFromClass: string, newToType: OperationType, newToClass: string) => {
    const newFromOptions =
      newFromType === "module" ? moduleOptions : subcatOptions;
    setFromOptions(newFromOptions);
    if (newFromClass !== fromClass) {
      handleFromChange([]);
    }

    if (newFromOptions.find((option) => option.key === newFromClass) === undefined) {
      if (newFromOptions.length > 0) {
        setFromClass(newFromOptions[0].key);
      } else {
        setFromClass("");
      }
    }

    const newToOptions = getToOptions(newFromClass, newToType);
    setToOptions(newToOptions);

    if (newToOptions.find((option) => option.key === newToClass) === undefined) {
      if (newToOptions.length > 0) {
        setToClass(newToOptions[0].key);
      } else {
        setToClass("");
      }
    } else {
      setToClass(newToClass);
    }
  };

  useEffect(() => {
    update(fromType, fromClass, toType, toClass);
  }, [fromType, toType]);

  useEffect(() => {
    update(fromType, fromClass, toType, toClass);
    if (fromClass == "module" || fromClass == "subcat") {
      setInputMethod("manual");
    }
    setCandidateComputed(false);
  }, [fromClass]);

  useEffect(() => {
    if (!isComputed) {
      setIndecs([]);
      setCandidates([]);
      setFrom([]);
      setFromType("module");
      setToType("module");
      setResult(undefined);
    }
  }, [isComputed]);

  return (
    <>
      {!isComputed ? <UpdateButton onClick={getIndecs} />
        : (
          <>
            <Grid
              container
              spacing={2}
              mb={2}
            >
              <Grid item xs={6}>
                <FormControl sx={{ m: 1 }}>
                  <FormLabel>Convert from</FormLabel>
                  <RadioGroup
                    defaultValue="module"
                    onChange={(e) => setFromType(e.target.value as OperationType)}
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
                    onChange={(e) => setToType(e.target.value as OperationType)}
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
                  options={fromOptions}
                  selected={fromClass}
                  setSelected={setFromClass}
                />
              </Grid>
              <Grid item xs={6}>
                <Combo
                  title="To"
                  options={toOptions}
                  selected={toClass}
                  setSelected={setToClass}
                />
              </Grid>
            </Grid>
            <Typography mb={2}>
              Operation: {operations[fromClass][toClass] ?? ""}
            </Typography>
            <FormControl sx={{ m: 1, mb: 2 }}>
              <FormLabel>How to input?</FormLabel>
              <RadioGroup
                defaultValue="candidates"
                onChange={(e) => {
                  setInputMethod(e.target.value as OperationType);
                  setCandidateComputed(false);
                }}
                value={inputMethod}
                row
              >
                <FormControlLabel
                  value="candidates"
                  control={<Radio />}
                  label="Select from candidates"
                  disabled={fromClass == "module" || fromClass == "subcat"}
                />
                <FormControlLabel
                  value="manual"
                  control={<Radio />}
                  label="Manually enter input"
                />
              </RadioGroup>
            </FormControl>
            <Box mb={2}>
              {inputMethod === "manual" ?
                <LargeList
                  multiple
                  header="Input (blue), validity not checked!"
                  data={indecs}
                  onChange={handleFromChange}
                  selected={forcedInput}
                />
                :
                !candidateComputed ?
                  <UpdateButton
                    title="Get candidates"
                    onClick={() => {
                      getCandidates(fromType, fromClass);
                    }}
                  /> :
                  <ListWithIndecList
                    candidates={candidates}
                    handleChange={handleFromChange}
                    leftHeader="Input (blue)"
                  />
              }
            </Box>
            {(inputMethod === "manual" || candidateComputed) &&
              <Grid container
                justifyContent="space-around"
                mb={2}>
                <Grid item xs={6} display="flex" justifyContent="center">
                  <Button variant="outlined" startIcon={<ArrowDownwardIcon />} onClick={fetchResult}>
                    Convert
                  </Button>
                </Grid>
                <Grid item xs={6} display="flex" justifyContent="center">
                  <Button startIcon={<ArrowUpwardIcon />} variant="outlined" onClick={handleSendInput}
                    disabled={result === undefined}
                  >
                    Send result to input
                  </Button>
                </Grid>
              </Grid>
            }
            {result !== undefined &&
              <Box mb={2}>
                <LargeList
                  header="Converted result (red):"
                  data={result}
                />
              </Box>
            }
          </>
        )}
    </>
  );
}
